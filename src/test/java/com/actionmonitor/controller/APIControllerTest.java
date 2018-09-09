package com.actionmonitor.controller;

import com.actionmonitor.domain.ActiveMQMessage;
import com.actionmonitor.repository.MessageRepository;
import com.actionmonitor.service.MessageService;
import org.apache.camel.CamelContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashSet;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {APIControllerTest.Initializer.class})
public class APIControllerTest {

    private static final String DEFAULT_MESSAGE = "DEFAULT_MESSAGE";
    private static final String DEFAULT_USER_LOGIN = "user1";
    private static final String DEFAULT_USER_PASSWORD = "user1";

    @ClassRule
    public static MySQLContainer mySQLContainer = new MySQLContainer()
            .withDatabaseName("dummy")
            .withUsername("dummy")
            .withPassword("dummy");

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @Autowired
    private CamelContext camelContext;

    @Autowired
    @Qualifier("sessionRegistry")
    private SessionRegistry sessionRegistry;

    private MockMvc restCategoryMockMvc;

    @BeforeClass
    public static void init() throws Exception {
        Connection connection = DriverManager.getConnection(mySQLContainer.getJdbcUrl(), mySQLContainer.getUsername(), mySQLContainer.getPassword());
        connection.prepareStatement("create table ACTIVEMQ_MSGS(id bigint primary key, container varchar(128), msgid_prod varchar(128), msgid_seq bigint, expiration bigint, msg blob, priority bigint, xid varchar(128))").execute();
    }

    @Before
    public void mock() {
        MockitoAnnotations.initMocks(this);
        final APIController apiController = new APIController(camelContext, sessionRegistry, messageService);
        restCategoryMockMvc = MockMvcBuilders.standaloneSetup(apiController).build();
    }

    @Test
    @Transactional
    public void testGetMessages() throws Exception {
        ActiveMQMessage activeMQMessage = new ActiveMQMessage();
        activeMQMessage.setId(1L);
        activeMQMessage.setMsg(DEFAULT_MESSAGE.getBytes());
        messageRepository.saveAndFlush(activeMQMessage);

        restCategoryMockMvc.perform(get("/messages"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(activeMQMessage.getId().intValue()));
    }

    @Test
    public void testGetNoUsers() throws Exception {
        restCategoryMockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.*").value(hasSize(0)));
    }

    @Test
    public void testGetUsers() throws Exception {
        User user = new User(DEFAULT_USER_LOGIN, DEFAULT_USER_PASSWORD, new HashSet<>());
        sessionRegistry.registerNewSession(UUID.randomUUID().toString(), user);

        restCategoryMockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.*").value(hasSize(1)))
                .andExpect(jsonPath("$.[*]").value(DEFAULT_USER_LOGIN));
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + mySQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + mySQLContainer.getUsername(),
                    "spring.datasource.password=" + mySQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

}
