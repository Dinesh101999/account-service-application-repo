package com.example.accountservice.exceptionhandler;

public class PaymentDeclineException extends RuntimeException {
    public PaymentDeclineException(String message) {
        super(message);
    }
}
