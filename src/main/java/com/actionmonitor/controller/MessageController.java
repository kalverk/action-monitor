package com.actionmonitor.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageController(SimpMessagingTemplate messageTemplate) {
        this.simpMessagingTemplate = messageTemplate;
    }

    @MessageMapping("/messaging")
    public void registerUser(Message<Object> message) {
        Principal principal = message.getHeaders().get(SimpMessageHeaderAccessor.USER_HEADER, Principal.class);
        if (principal == null) {
            logger.error("Unauthenticated user");
            return;
        }

        logger.debug("User has joined '{}'", principal.getName());
        simpMessagingTemplate.convertAndSend("/topic/users", principal.getName());
    }
}