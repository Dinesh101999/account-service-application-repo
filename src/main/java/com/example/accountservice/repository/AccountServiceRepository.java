package com.example.accountservice.repository;

import com.example.accountservice.constant.TransferStatus;
import com.example.accountservice.model.AccountService;
import com.example.accountservice.model.Transfer;
import feign.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AccountServiceRepository extends JpaRepository<AccountService , Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from AccountService a where a.id = :id")
    Optional<AccountService> findByIdForUpdate(@Param("id") Long id);
}
