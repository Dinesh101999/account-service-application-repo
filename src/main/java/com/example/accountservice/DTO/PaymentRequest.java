package com.example.accountservice.DTO;

import java.math.BigDecimal;

public record PaymentRequest(String idempotencyKey , Long from , Long to , BigDecimal amount) {
}
