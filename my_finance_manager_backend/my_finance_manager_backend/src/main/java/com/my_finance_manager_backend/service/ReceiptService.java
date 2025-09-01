package com.my_finance_manager_backend.service;

import com.my_finance_manager_backend.model.Receipt;
import com.my_finance_manager_backend.repository.ReceiptRepository;
import com.my_finance_manager_backend.service.GeminiAIService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ReceiptService {

    private final ReceiptRepository repo;
    private final GeminiAIService geminiAIService;

    public ReceiptService(ReceiptRepository repo, GeminiAIService geminiAIService) {
        this.repo = repo;
        this.geminiAIService = geminiAIService;
    }

    // Save receipt metadata and process with AI
    public Receipt saveReceipt(MultipartFile file) throws IOException {
        // Validate file first
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        
        try {
            // Process file with Gemini AI
            Map<String, Object> aiResults = geminiAIService.extractInformationFromFile(file);
            
            // Build receipt with extracted information
            Receipt receipt = Receipt.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .processed(true)
                    .extractedText((String) aiResults.get("extractedText"))
                    .merchantName((String) aiResults.get("merchantName"))
                    .amount((Double) aiResults.get("amount"))
                    .currency((String) aiResults.get("currency"))
                    .transactionDate((String) aiResults.get("transactionDate"))
                    .category((String) aiResults.get("category"))
                    .description((String) aiResults.get("description"))
                    .aiConfidence((String) aiResults.get("aiConfidence"))
                    .build();
            
            return repo.save(receipt);
            
        } catch (Exception e) {
            // If AI processing fails, save basic receipt info
            Receipt receipt = Receipt.builder()
                    .fileName(file.getOriginalFilename())
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .uploadedAt(LocalDateTime.now())
                    .processed(false)
                    .extractedText("AI processing failed: " + e.getMessage())
                    .build();
            
            return repo.save(receipt);
        }
    }
    
    // Reprocess receipt with AI (in case of failure)
    public Receipt reprocessReceipt(Long id) throws IOException {
        Receipt receipt = getReceiptById(id);
        
        try {
            // Get the original file from storage (this would need to be implemented)
            // For now, we'll try to reprocess with existing data
            
            // Create a mock file for reprocessing
            // In a real implementation, you'd retrieve the actual file from storage
            
            // Try to extract more information from the existing extracted text
            if (receipt.getExtractedText() != null && !receipt.getExtractedText().isEmpty()) {
                // Process the extracted text again with AI
                Map<String, Object> aiResults = geminiAIService.extractInformationFromFile(
                    createMockMultipartFile(receipt.getFileName(), receipt.getFileType(), receipt.getExtractedText())
                );
                
                // Update receipt with new AI results
                receipt.setProcessed(true);
                receipt.setMerchantName((String) aiResults.get("merchantName"));
                receipt.setAmount((Double) aiResults.get("amount"));
                receipt.setCurrency((String) aiResults.get("currency"));
                receipt.setTransactionDate((String) aiResults.get("transactionDate"));
                receipt.setCategory((String) aiResults.get("category"));
                receipt.setDescription((String) aiResults.get("description"));
                receipt.setAiConfidence((String) aiResults.get("aiConfidence"));
                
                return repo.save(receipt);
            }
            
            return receipt;
            
        } catch (Exception e) {
            receipt.setProcessed(false);
            receipt.setExtractedText("Reprocessing failed: " + e.getMessage());
            return repo.save(receipt);
        }
    }
    
    // Helper method to create a mock MultipartFile for reprocessing
    private MultipartFile createMockMultipartFile(String fileName, String contentType, String content) {
        return new MultipartFile() {
            @Override
            public String getName() { return "file"; }
            
            @Override
            public String getOriginalFilename() { return fileName; }
            
            @Override
            public String getContentType() { return contentType; }
            
            @Override
            public boolean isEmpty() { return content == null || content.isEmpty(); }
            
            @Override
            public long getSize() { return content != null ? content.getBytes().length : 0; }
            
            @Override
            public byte[] getBytes() throws IOException { 
                return content != null ? content.getBytes() : new byte[0]; 
            }
            
            @Override
            public java.io.InputStream getInputStream() throws IOException { 
                return new java.io.ByteArrayInputStream(getBytes()); 
            }
            
            @Override
            public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                // Not implemented for mock
            }
        };
    }
    
    // Get receipt by ID
    public Receipt getReceiptById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Receipt not found with id: " + id));
    }
    
    // Get all receipts
    public List<Receipt> getAllReceipts() {
        return repo.findAll();
    }
    
    // Delete receipt
    public void deleteReceipt(Long id) {
        repo.deleteById(id);
    }
}
