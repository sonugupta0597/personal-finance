package com.my_finance_manager_backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "receipts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Receipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String fileType;

    private Long fileSize;

    private LocalDateTime uploadedAt;

    @Builder.Default
    private boolean processed = false; // true when OCR is done
    
    // Extracted information from Gemini AI
    private String extractedText;
    private String merchantName;
    private Double amount;
    private String currency;
    private String transactionDate;
    private String category;
    private String description;
    private String aiConfidence;
}
