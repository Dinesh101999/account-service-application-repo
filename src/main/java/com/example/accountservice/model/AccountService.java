package com.example.accountservice.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name="accountService")
@NoArgsConstructor
@AllArgsConstructor
@Data
public class AccountService {

    @Id
    private Long id;

    @Column(nullable = false , precision = 15, scale = 2)
    private String accountHolderName;

    @Column(nullable = false , precision = 12 , scale = 2)
    private BigDecimal balance;

}
