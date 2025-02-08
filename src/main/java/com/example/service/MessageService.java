package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Message;
import com.example.exception.InvalidMessageException;
import com.example.repository.MessageRepository; 
import com.example.repository.AccountRepository;
import java.util.List;
import javax.transaction.Transactional;
import com.example.exception.UserNotFoundException;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Message createMessage(Message message) {
        if (message.getMessageText() == null || message.getMessageText().isEmpty() || message.getMessageText().length() > 255) {
            return null; // Invalid message
        }
    
        // Check if the user exists
        if (!accountRepository.existsById(message.getPostedBy().intValue())) {
            return null; // ✅ Ensure missing user returns null
        }
    
        // Save message
        Message savedMessage = messageRepository.save(message);
    
        if (savedMessage == null || savedMessage.getMessageId() == 0) {
            return null;
        }
    
        return savedMessage;
    }
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public Message getMessageById(int messageId) {
        return messageRepository.findById(messageId).orElse(null);
    }

    public int deleteMessage(int messageId) {
        int rowsUpdated = 0;
        if (messageRepository.existsById(messageId)) {
            messageRepository.deleteById(messageId);
            rowsUpdated = 1;
        }
        return rowsUpdated;
    }

    public int updateMessage(int messageId, Message message) {
        if (message.getMessageText() == null || message.getMessageText().isEmpty() || message.getMessageText().length() > 255) {
            throw new RuntimeException("Invalid message data");
        }
        if (messageRepository.existsById(messageId)) {
            Message existingMessage = messageRepository.findById(messageId).get();
            existingMessage.setMessageText(message.getMessageText());
            messageRepository.save(existingMessage);
            return 1;
        }
        return 0;
    }

    public List<Message> getMessagesByAccountId(int accountId) {
        return messageRepository.findByPostedBy(accountId);
}
}