package com.example.accountservice.fallbackfactory;

import com.example.accountservice.downstreaminterface.PaymentClient;
import com.example.accountservice.exceptionhandler.PaymentDeclineException;
import com.example.accountservice.exceptionhandler.PaymentUnavailableException;
import feign.FeignException;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class PaymentClientFallbackFactory implements FallbackFactory<PaymentClient> {

    @Override
    public PaymentClient create(Throwable cause) {
        return (key, req) -> {
            if (cause instanceof FeignException fe && fe.status() >= 400 && fe.status() < 500) {
                throw new PaymentDeclineException("Payment declined (" + fe.status() + ")");
            }
            throw new PaymentUnavailableException("Payment service unavailable", cause);
        };
    }
}
