package com.example.accountservice.controller;


import com.example.accountservice.model.TransferRequest;
import com.example.accountservice.service.AccountServiceServiceLayer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountServiceController {

    private final AccountServiceServiceLayer accountServiceServiceLayer;

    public AccountServiceController (AccountServiceServiceLayer accountServiceServiceLayer){
        this.accountServiceServiceLayer = accountServiceServiceLayer;
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transferMoney(@RequestBody TransferRequest transferRequest){
        String response = accountServiceServiceLayer.transferMoney(transferRequest.getFromAccountId() ,
                transferRequest.getToAccountId(), transferRequest.getAmount());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{account}/balance")
    public ResponseEntity<BigDecimal> getBalance(@PathVariable Long receiverId){
        BigDecimal balanceAmount = accountServiceServiceLayer.getBalance(receiverId);
        return ResponseEntity.ok(balanceAmount);
    }
}
