package com.example.accountservice.exceptionhandler;

public class PaymentUnavailableException extends RuntimeException {
    public PaymentUnavailableException(String message, Throwable cause) {
        super(message);
    }
}
