package com.my_finance_manager_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillScanResultDTO {

    // File Information
    @JsonProperty("fileName")
    private String fileName;
    
    @JsonProperty("fileType")
    private String fileType;
    
    @JsonProperty("fileSize")
    private Long fileSize;
    
    @JsonProperty("scanTimestamp")
    private LocalDateTime scanTimestamp;

    // Extracted Information
    @JsonProperty("merchantName")
    private String merchantName;
    
    @JsonProperty("amount")
    private Double amount;
    
    @JsonProperty("currency")
    private String currency;
    
    @JsonProperty("transactionDate")
    private String transactionDate;
    
    @JsonProperty("category")
    private String category;
    
    @JsonProperty("description")
    private String description;
    
    @JsonProperty("invoiceNumber")
    private String invoiceNumber;
    
    @JsonProperty("taxAmount")
    private Double taxAmount;
    
    @JsonProperty("totalAmount")
    private Double totalAmount;
    
    @JsonProperty("paymentMethod")
    private String paymentMethod;
    
    @JsonProperty("items")
    private List<String> items;

    // AI Processing Information
    @JsonProperty("extractedText")
    private String extractedText;
    
    @JsonProperty("aiConfidence")
    private String aiConfidence;
    
    @JsonProperty("processingStatus")
    private String processingStatus;
    
    @JsonProperty("errorMessage")
    private String errorMessage;

    // Helper methods
    public boolean isSuccessfullyProcessed() {
        return processingStatus != null && processingStatus.equals("SUCCESS");
    }
    
    public boolean hasAmount() {
        return amount != null && amount > 0;
    }
    
    public boolean hasMerchant() {
        return merchantName != null && !merchantName.trim().isEmpty();
    }
    
    public boolean hasDate() {
        return transactionDate != null && !transactionDate.trim().isEmpty();
    }
    
    public String getFormattedAmount() {
        if (amount != null && currency != null) {
            return currency + " " + String.format("%.2f", amount);
        } else if (amount != null) {
            return String.format("%.2f", amount);
        }
        return "N/A";
    }
    
    public String getFormattedTaxAmount() {
        if (taxAmount != null && taxAmount > 0) {
            return currency != null ? currency + " " + String.format("%.2f", taxAmount) : String.format("%.2f", taxAmount);
        }
        return "N/A";
    }
    
    public String getFormattedTotalAmount() {
        if (totalAmount != null && totalAmount > 0) {
            return currency != null ? currency + " " + String.format("%.2f", totalAmount) : String.format("%.2f", totalAmount);
        }
        return getFormattedAmount();
    }
    
    public String getCategoryDisplayName() {
        if (category != null && !category.trim().isEmpty()) {
            return category;
        }
        return "Uncategorized";
    }
    
    public String getMerchantDisplayName() {
        if (merchantName != null && !merchantName.trim().isEmpty()) {
            return merchantName;
        }
        return "Unknown Merchant";
    }
    
    public String getDateDisplayName() {
        if (transactionDate != null && !transactionDate.trim().isEmpty()) {
            return transactionDate;
        }
        return "Date not found";
    }
    
    public List<String> getItemsList() {
        if (items != null && !items.isEmpty()) {
            return items;
        }
        return List.of("No items listed");
    }
    
    public String getProcessingSummary() {
        if (isSuccessfullyProcessed()) {
            return String.format("Successfully processed %s with %s confidence", 
                fileName != null ? fileName : "file", 
                aiConfidence != null ? aiConfidence : "unknown");
        } else {
            return String.format("Processing failed: %s", 
                errorMessage != null ? errorMessage : "Unknown error");
        }
    }
}
