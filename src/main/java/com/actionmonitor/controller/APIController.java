package com.actionmonitor.controller;

import com.actionmonitor.domain.ActiveMQMessage;
import com.actionmonitor.dto.MessageDTO;
import com.actionmonitor.service.MessageService;
import org.apache.camel.CamelContext;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class APIController {

    @Value("${broker.queue.name:messages}")
    private String messageQueueName;

    @Value("${db.queue.name:db_messages}")
    private String dbQueueName;

    private final CamelContext camelContext;

    @Qualifier("sessionRegistry")
    private final SessionRegistry sessionRegistry;

    private final MessageService messageService;

    public APIController(CamelContext camelContext, SessionRegistry sessionRegistry, MessageService messageService) {
        this.camelContext = camelContext;
        this.sessionRegistry = sessionRegistry;
        this.messageService = messageService;
    }

    /**
     * Receive the messages and pass them to ActiveMQ.
     *
     * @param message the message to send, encapsulated in a wrapper
     */
    @PostMapping(value = "/send", consumes = "application/json")
    public void sendMessage(@RequestBody MessageDTO message, Principal currentUser) {
        message.setFrom(currentUser.getName());
        camelContext.createProducerTemplate().sendBody(String.format("activemq:%s", messageQueueName), message);
        camelContext.createProducerTemplate().sendBody(String.format("activemq:%s", dbQueueName), message);
    }

    /**
     * Returns the names of the currently logged in users.
     *
     * @return set of user names
     */
    @GetMapping(value = "/users", produces = "application/json")
    public Set<String> getUsers() {
        return sessionRegistry.getAllPrincipals().stream().map(u -> ((User) u).getUsername()).collect(Collectors.toSet());
    }

    /**
     * Returns persisted messages
     *
     * @return list of messages
     */
    @GetMapping(value = "/messages", produces = "application/json")
    public List<ActiveMQMessage> getMessages() {
        return messageService.getAllMessages();
    }
}
