package com.my_finance_manager_backend.controller;

import com.my_finance_manager_backend.service.GeminiAIService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/bill-scan")
public class BillScanController {

    private final GeminiAIService geminiAIService;

    public BillScanController(GeminiAIService geminiAIService) {
        this.geminiAIService = geminiAIService;
    }

    /**
     * Scan and extract information from uploaded bills/receipts
     * Supports both images (JPEG, PNG) and PDFs
     */
    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scanBill(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File is empty"));
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File size exceeds 10MB limit"));
            }

            // Check file type
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.startsWith("image/") && !contentType.equals("application/pdf"))) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Unsupported file type. Only images (JPEG, PNG) and PDFs are supported."));
            }

            // Process file with Gemini AI
            Map<String, Object> extractedInfo = geminiAIService.extractInformationFromFile(file);
            
            // Add file metadata
            extractedInfo.put("fileName", file.getOriginalFilename());
            extractedInfo.put("fileType", file.getContentType());
            extractedInfo.put("fileSize", file.getSize());
            extractedInfo.put("scanTimestamp", System.currentTimeMillis());

            return ResponseEntity.ok(extractedInfo);

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "File processing failed: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "AI processing was interrupted"));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Unexpected error: " + e.getMessage()));
        }
    }

    /**
     * Get supported file types and size limits
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getScanInfo() {
        Map<String, Object> info = Map.of(
            "supportedFormats", new String[]{"JPEG", "PNG", "PDF"},
            "maxFileSize", "10MB",
            "features", new String[]{
                "Merchant name extraction",
                "Amount and currency detection",
                "Date recognition",
                "Category classification",
                "Invoice number extraction",
                "Tax amount detection",
                "Payment method identification",
                "Item list extraction"
            },
            "aiModel", "Gemini 1.5 Flash (PDF) + Gemini Pro Vision (Images)",
            "processingTime", "2-5 seconds"
        );
        
        return ResponseEntity.ok(info);
    }

    /**
     * Health check for the scanning service
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "healthy",
            "service", "Bill Scan Service",
            "aiProvider", "Google Gemini",
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(health);
    }
}
