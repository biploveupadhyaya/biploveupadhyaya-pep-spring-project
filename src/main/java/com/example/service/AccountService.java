package com.example.service;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.entity.Account; 
import com.example.repository.AccountRepository; 
import com.example.exception.InvalidAccountException;
import com.example.exception.UserAlreadyExistsException;
@Service
@Transactional
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;
   
    public Account createAccount(Account account) {
        if (account.getUsername() == null || account.getUsername().isEmpty() || account.getPassword().length() < 4) {
            throw new InvalidAccountException("Username cannot be null or empty, and password must be at least 4 characters.");
        }
        if (accountRepository.findByUsername(account.getUsername()) != null) {
            throw new UserAlreadyExistsException("Username already exists.");
        }
        return accountRepository.save(account);
    }

    public Account login(Account account) {
        Account existingAccount = accountRepository.findByUsername(account.getUsername());
        if (existingAccount != null && existingAccount.getPassword().equals(account.getPassword())) {
            return existingAccount;
        }
        throw new RuntimeException("Invalid username or password");
    }
    public Account findByUsername(String username) {
        return accountRepository.findByUsername(username);
    }
    public boolean userExists(int userId) {
        return accountRepository.findById(userId).isPresent();
    }
}