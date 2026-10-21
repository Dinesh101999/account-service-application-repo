package com.example.accountservice.service;


import com.example.accountservice.DTO.PaymentRequest;
import com.example.accountservice.constant.TransferStatus;
import com.example.accountservice.downstreaminterface.PaymentClient;
import com.example.accountservice.exceptionhandler.AccountNotFoundException;
import com.example.accountservice.exceptionhandler.InsufficientBalanceException;
import com.example.accountservice.exceptionhandler.PaymentDeclineException;
import com.example.accountservice.exceptionhandler.PaymentUnavailableException;
import com.example.accountservice.model.AccountService;
import com.example.accountservice.model.Transfer;
import com.example.accountservice.repository.AccountServiceRepository;
import com.example.accountservice.repository.TransferRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceServiceLayer {

    private final AccountServiceRepository accountServiceRepository;

    private final TransferRepository transferRepository;

    private final PaymentClient paymentClient;

    private final TransactionTemplate tx;

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
     public AccountService addAccountUser(AccountService accountService){
         return accountServiceRepository.save(accountService);
     }


     public String transferMoneyWithSaga(String idempotencyKey , Long fromId, Long toId, BigDecimal amount){
        if(amount ==  null || amount.compareTo(BigDecimal.ZERO) <=0){
            throw new IllegalArgumentException("transfer amount must be greater than Zero");
        }
        if(fromId.equals(toId)){
            throw new IllegalArgumentException("Source and destination accounts must be different");
        }
         Optional<Transfer> existing = transferRepository.findByIdempotencyKey(idempotencyKey);
        if(existing.isPresent()){
            return resume(existing.get());
        }
        Transfer t;
        try{
            t = tx.execute(status -> debitSender(idempotencyKey ,fromId ,toId ,amount));
        }catch(DataIntegrityViolationException duplicate){
            return resume(transferRepository.findByIdempotencyKey(idempotencyKey).orElseThrow());
        }
        return pay(t);
     }

     private Transfer debitSender(String key , Long fromId , Long toId, BigDecimal amount){
        Long first = Math.min(fromId , toId);
        Long second = Math.max(fromId , toId);
        AccountService a1 = lock(first);
        AccountService a2 = lock(second);
        AccountService sender = fromId.equals(first) ? a1 : a2;
        if(sender.getBalance().compareTo(amount)<0){
            throw  new InsufficientBalanceException("Insufficient balance");
        }
        sender.setBalance(sender.getBalance().subtract(amount));
        accountServiceRepository.save(sender);

        Transfer t = new Transfer();
        t.setIdempotencyKey(key);
        t.setFromAccountId(fromId);
        t.setToAccountId(toId);
        t.setAmount(amount);
        t.setStatus(TransferStatus.DEBITED);
        return transferRepository.saveAndFlush(t);
     }
     public String pay(Transfer t){
        try{
            paymentClient.processPayment(t.getIdempotencyKey(),new PaymentRequest(t.getIdempotencyKey() , t.getFromAccountId(),
                    t.getToAccountId(),t.getAmount()));
        }catch (PaymentDeclineException e){
            tx.executeWithoutResult(s -> compensate(t.getId()));
            throw e;
        }catch(PaymentUnavailableException e){
            tx.executeWithoutResult(s -> setStatus(t.getId(), TransferStatus.PAYMENT_UNKNOWN) );
            throw  e;
        }
        tx.executeWithoutResult(s -> complete(t.getId()));
        return "Money transferred successfully";
     }

     private void complete(Long transferId){
        Transfer t  = transferRepository.findById(transferId).orElseThrow();
        if(t.getStatus() == TransferStatus.COMPLETED) return;
        AccountService receiver = lock(t.getToAccountId());
        receiver.setBalance(receiver.getBalance().add(t.getAmount()));
        accountServiceRepository.save(receiver);
        setStatus(t.getId(),TransferStatus.COMPLETED);
     }

     private void compensate(Long transferId){
        Transfer t = transferRepository.findById(transferId).orElseThrow();
        if(t.getStatus() == TransferStatus.COMPENSATED) return;
        AccountService sender = lock(t.getFromAccountId());
        sender.setBalance(sender.getBalance().add(t.getAmount()));
        accountServiceRepository.save(sender);
        setStatus(t.getId(), TransferStatus.COMPENSATED);
     }
     private String resume(Transfer t){
        return switch (t.getStatus()){
            case COMPLETED -> "Money Transferred Successfully";
            case COMPENSATED -> throw new PaymentDeclineException("Transfer failed and was refunded");
            case DEBITED , PAYMENT_UNKNOWN ->  pay(t);
        };
     }

     private AccountService lock(Long id){
        return accountServiceRepository.findByIdForUpdate(id).orElseThrow(()
                -> new AccountNotFoundException("Account" + id + "not found"));
     }

     private void setStatus(Long id , TransferStatus transferStatus){
        setStatus(transferRepository.findById(id).orElseThrow() , transferStatus);
     }
     private void setStatus(Transfer t , TransferStatus s){
        t.setStatus(s);
        t.setUpdateAt(Instant.now());
        transferRepository.save(t);
     }
}
