package com.actionmonitor.service;

import com.actionmonitor.domain.ActiveMQMessage;
import com.actionmonitor.repository.MessageRepository;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = {MessageServiceTest.Initializer.class})
public class MessageServiceTest {

    private static final String DEFAULT_MESSAGE = "DEFAULT_MESSAGE";

    @ClassRule
    public static MySQLContainer mySQLContainer = new MySQLContainer()
            .withDatabaseName("dummy")
            .withUsername("dummy")
            .withPassword("dummy");

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private MessageService messageService;

    @BeforeClass
    public static void init() throws SQLException {
        Connection connection = DriverManager.getConnection(mySQLContainer.getJdbcUrl(), mySQLContainer.getUsername(), mySQLContainer.getPassword());
        connection.prepareStatement("create table ACTIVEMQ_MSGS(id bigint primary key, container varchar(128), msgid_prod varchar(128), msgid_seq bigint, expiration bigint, msg blob, priority bigint, xid varchar(128))").execute();
    }

    @Test
    @Transactional
    public void testGetAllMessages() {
        ActiveMQMessage activeMQMessage = new ActiveMQMessage();
        activeMQMessage.setId(1L);
        activeMQMessage.setMsg(DEFAULT_MESSAGE.getBytes());
        messageRepository.saveAndFlush(activeMQMessage);

        List<ActiveMQMessage> allMessages = messageService.getAllMessages();
        assertEquals(1, allMessages.size());

        ActiveMQMessage activeMQMessage1 = allMessages.get(0);
        assertEquals(DEFAULT_MESSAGE, new String(activeMQMessage1.getMsg(), StandardCharsets.UTF_8));
        assertEquals(activeMQMessage.getId(), activeMQMessage1.getId());
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
