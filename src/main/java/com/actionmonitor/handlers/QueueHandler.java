package com.actionmonitor.handlers;

import com.actionmonitor.dto.MessageDTO;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeTypeUtils;

import java.util.HashMap;
import java.util.Map;

@Component(value = "queueHandler")
public class QueueHandler {

    private final SimpMessageSendingOperations simpMessageSendingOperations;

    private static Map<String, Object> defaultHeaders;

    static {
        defaultHeaders = new HashMap<>();
        defaultHeaders.put(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON);
    }

    public QueueHandler(SimpMessageSendingOperations msgTemplate) {
        this.simpMessageSendingOperations = msgTemplate;
    }

    public void handle(Exchange exchange) {
        Message camelMessage = exchange.getIn();
        MessageDTO message = camelMessage.getBody(MessageDTO.class);
        simpMessageSendingOperations.convertAndSendToUser(message.getTo(), "/topic/messages", message, defaultHeaders);
    }
}

