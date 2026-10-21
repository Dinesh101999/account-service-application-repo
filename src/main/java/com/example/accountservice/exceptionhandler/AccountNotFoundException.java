package com.example.accountservice.exceptionhandler;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String m ){super(m);}
}
