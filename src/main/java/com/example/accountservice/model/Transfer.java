package com.example.accountservice.model;


import com.example.accountservice.constant.TransferStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "transfers" , uniqueConstraints = @UniqueConstraint(columnNames = "idempotency_key"))
@Data
@Getter  @Setter @NoArgsConstructor
public class Transfer {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "idempotency_key" , nullable = false , updatable = false)
    private String idempotencyKey;

    private Long fromAccountId;

    private Long toAccountId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    private Instant updateAt = Instant.now();
}
