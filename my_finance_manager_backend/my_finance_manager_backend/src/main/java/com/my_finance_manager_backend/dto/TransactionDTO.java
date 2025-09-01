package com.my_finance_manager_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDTO {
    private LocalDate date;
    private String description;
    private BigDecimal amount;
    private String type; // "INCOME" or "EXPENSE"
    private String category;
}
