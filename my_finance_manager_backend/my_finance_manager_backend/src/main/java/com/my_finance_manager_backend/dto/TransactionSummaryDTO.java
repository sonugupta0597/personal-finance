package com.my_finance_manager_backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionSummaryDTO {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;

    private Map<String, BigDecimal> incomeBySource;
    private Map<String, BigDecimal> expenseByCategory;
}
