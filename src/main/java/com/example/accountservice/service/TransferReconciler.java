package com.example.accountservice.service;


import com.example.accountservice.constant.TransferStatus;
import com.example.accountservice.model.Transfer;
import com.example.accountservice.repository.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class TransferReconciler {

    private final TransferRepository transferRepository;
    private final AccountServiceServiceLayer serviceServiceLayer;

    public TransferReconciler(TransferRepository transferRepository, AccountServiceServiceLayer serviceServiceLayer) {
        this.transferRepository = transferRepository;
        this.serviceServiceLayer = serviceServiceLayer;
    }

    @Scheduled(fixedDelay = 60_000)
    public void recover(){
        Instant cutOff = Instant.now().minusSeconds(30);
        for(TransferStatus status : List.of(TransferStatus.DEBITED , TransferStatus.PAYMENT_UNKNOWN)){
            for(Transfer t : transferRepository.findByStatus(status)){
                if(t.getUpdateAt().isBefore(cutOff)){
                    try{
                        serviceServiceLayer.pay(t);
                    }catch (Exception e){
                        throw new RuntimeException("Still its failing");
                    }
                }
            }
        }
    }
}
