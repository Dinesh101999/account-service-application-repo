package com.example.accountservice.exceptionhandler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AccountNotFoundException.class)
    ResponseEntity<String> notFound(RuntimeException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler({IllegalArgumentException.class , InsufficientBalanceException.class, PaymentDeclineException.class})
    ResponseEntity<String> badRequest(RuntimeException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler(PaymentUnavailableException.class)
    ResponseEntity<String> unavailable(RuntimeException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
