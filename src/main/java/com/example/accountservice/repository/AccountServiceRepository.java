package com.example.accountservice.repository;

import com.example.accountservice.model.AccountService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountServiceRepository extends JpaRepository<AccountService , Long> {
}
