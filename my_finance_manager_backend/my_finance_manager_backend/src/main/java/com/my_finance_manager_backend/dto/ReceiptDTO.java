package com.my_finance_manager_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;
    private boolean processed;
    
    // AI extracted information
    private String extractedText;
    private String merchantName;
    private Double amount;
    private String currency;
    private String transactionDate;
    private String category;
    private String description;
    private String aiConfidence;
    
    // Processing status
    private String status;
    private String errorMessage;
}
