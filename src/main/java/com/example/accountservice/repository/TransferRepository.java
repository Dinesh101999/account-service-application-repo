package com.example.accountservice.repository;

import com.example.accountservice.constant.TransferStatus;
import com.example.accountservice.model.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransferRepository extends JpaRepository<Transfer , Long> {
    Optional<Transfer> findByIdempotencyKey(String key);
    List<Transfer> findByStatus(TransferStatus status);
}
