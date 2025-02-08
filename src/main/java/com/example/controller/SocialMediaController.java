package com.example.controller;
import com.example.exception.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.entity.Account; 
import com.example.entity.Message; 
import com.example.service.AccountService;
import com.example.service.MessageService; 

@RestController
@RequestMapping("/")
public class SocialMediaController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private MessageService messageService;

    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account account) {
        // Check if the username already exists
        if (accountService.findByUsername(account.getUsername()) != null) {
            // Username exists, return 409 Conflict
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    
        Account createdAccount = accountService.createAccount(account);
        return ResponseEntity.status(HttpStatus.OK).body(createdAccount);
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account) {
        // Check if the username exists
        Account existingAccount = accountService.findByUsername(account.getUsername());
    
        if (existingAccount == null) {
            // Username does not exist, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        // Optionally, you may want to check if the password is correct (if applicable)
        // Assuming you want to validate the password as well
        if (!existingAccount.getPassword().equals(account.getPassword())) {
            // Password is incorrect, return 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    
        // If login is successful, return the account details
        return ResponseEntity.status(HttpStatus.OK).body(existingAccount);
    }

    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        if (message.getMessageText() == null || message.getMessageText().isBlank() || message.getMessageText().length() > 255) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    
        Message createdMessage = messageService.createMessage(message);
        
        if (createdMessage == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // ✅ Ensure 400 is returned for missing user
        }
    
        return ResponseEntity.ok(createdMessage); // ✅ Ensure 200 OK for success case
    }
    @GetMapping("/messages")
    public ResponseEntity<List<Message>> getAllMessages() {
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Message> getMessageById(@PathVariable int messageId) {
        Message message = messageService.getMessageById(messageId);
        return ResponseEntity.ok(message);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Object> deleteMessage(@PathVariable int messageId) {
        int rowsUpdated = messageService.deleteMessage(messageId);
        if (rowsUpdated == 0) {
            return ResponseEntity.ok().body(""); // Return empty body with 200 OK
        }
        return ResponseEntity.ok(rowsUpdated);
    }

    private static final int MAX_MESSAGE_LENGTH = 255;
    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@PathVariable int messageId, @RequestBody Message message) {
        // Check if the message exists
        Message existingMessage = messageService.getMessageById(messageId);
    
        if (existingMessage == null) {
            // Message does not exist, return 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    
        // Check if the messageText is empty
        if (message.getMessageText() == null || message.getMessageText().isEmpty()) {
            // Empty message text, return 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    
        // Check if the messageText is too long
        if (message.getMessageText().length() > MAX_MESSAGE_LENGTH) {
            // Message text is too long, return 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    
        // If the message exists and messageText is valid, update it and return the number of rows updated
        int rowsUpdated = messageService.updateMessage(messageId, message);
        return ResponseEntity.ok(rowsUpdated);
    }
    

    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> getMessagesByAccountId(@PathVariable int accountId) {
        List<Message> messages = messageService.getMessagesByAccountId(accountId);
        return ResponseEntity.ok(messages);
    }
}