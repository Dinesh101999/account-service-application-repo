package com.example.accountservice.downstreaminterface;


import com.example.accountservice.DTO.PaymentRequest;
import com.example.accountservice.DTO.PaymentResponse;
import com.example.accountservice.fallbackfactory.PaymentClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "payment-clients", url= "${clients.payment.url}",
fallbackFactory = PaymentClientFallbackFactory.class, primary = false)
public interface PaymentClient {
    @PostMapping("/api/payments/process")
    PaymentResponse processPayment(@RequestHeader("Idempotency-Key") String key ,
                                   @RequestBody PaymentRequest request
    );
}
