package com.example.accountservice.downstreaminterface;


import com.example.accountservice.DTO.PaymentRequest;
import com.example.accountservice.DTO.PaymentResponse;
import com.example.accountservice.exceptionhandler.PaymentDeclineException;
import com.example.accountservice.exceptionhandler.PaymentUnavailableException;

import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;


@Component
@Profile("Mock")
@Primary
public class MockPaymentClient implements  PaymentClient{
    private final Map<String , Integer> attampts =  new ConcurrentHashMap<>();

    @Override
    public PaymentResponse processPayment(String key, PaymentRequest request) {
        BigDecimal amount = request.amount();
        if(amount.compareTo(new BigDecimal("666"))==0){
            throw new PaymentDeclineException("Mock : payment declined");
        }
        if(amount.compareTo(new BigDecimal("9999"))==0){
            int attmpts = attampts.merge(key , 1 , Integer::sum);
            if(attmpts==1){
                throw new PaymentUnavailableException("Mock : payment service timeout", null);
            }
        }
        return  new PaymentResponse("Success");
    }
}
