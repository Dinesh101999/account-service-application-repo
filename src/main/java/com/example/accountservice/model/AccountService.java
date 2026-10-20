package com.example.accountservice.model;


import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false , precision = 15, scale = 2)
    private String accountHolderName;

    @Column(nullable = false , precision = 12 , scale = 2)
    private BigDecimal balance;

}
