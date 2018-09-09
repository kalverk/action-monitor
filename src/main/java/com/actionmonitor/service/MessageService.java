package com.actionmonitor.service;

import com.actionmonitor.domain.ActiveMQMessage;
import com.actionmonitor.repository.MessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MessageService {

    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<ActiveMQMessage> getAllMessages() {
        return messageRepository.findAll();
    }

}
