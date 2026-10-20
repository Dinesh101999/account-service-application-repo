package com.example.accountservice.service;


import com.example.accountservice.model.AccountService;
import com.example.accountservice.repository.AccountServiceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceServiceLayer {

    private final AccountServiceRepository accountServiceRepository;

    public AccountServiceServiceLayer (AccountServiceRepository accountServiceRepository){
        this.accountServiceRepository = accountServiceRepository;
    }
     @Transactional
    public String transferMoney(Long fromAccountId , Long toAccountId , BigDecimal amount){
        if(amount == null || amount.compareTo(BigDecimal.ZERO)<=0){
            throw new IllegalArgumentException("transfer amount must be greater than Zero");
        }
        if(fromAccountId.equals(toAccountId)){
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
         AccountService sender = accountServiceRepository.findById(fromAccountId).orElseThrow(() ->
                 new RuntimeException("Sender account not found"));
        AccountService receiver = accountServiceRepository.findById(toAccountId).orElseThrow(() ->
                new RuntimeException("Receiver Account Not found"));
        if(sender.getBalance().compareTo(amount) <0){
            throw new RuntimeException("Insufficient balance");
        }
        sender.setBalance(sender.getBalance().subtract(amount));
        receiver.setBalance(receiver.getBalance().add(amount));

        accountServiceRepository.save(sender);
        accountServiceRepository.save(receiver);
        return "Money transferred successfully";
     }

     public BigDecimal getBalance(Long accountId){
        AccountService receiverBalance = accountServiceRepository.findById(accountId).orElseThrow(
                () -> new RuntimeException("Account not found")
        );
        return receiverBalance.getBalance();
     }
}
