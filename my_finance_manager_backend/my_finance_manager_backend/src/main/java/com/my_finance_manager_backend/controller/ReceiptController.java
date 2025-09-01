package com.my_finance_manager_backend.controller;

import com.my_finance_manager_backend.model.Receipt;
import com.my_finance_manager_backend.service.ReceiptService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/receipts")
public class ReceiptController {

    private final ReceiptService service;

    public ReceiptController(ReceiptService service) {
        this.service = service;
    }

    // Upload receipt
    @PostMapping("/upload")
    public ResponseEntity<Receipt> uploadReceipt(@RequestParam("file") MultipartFile file) throws IOException {
        try {
            Receipt receipt = service.saveReceipt(file);
            return ResponseEntity.ok(receipt);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
    
    // Get receipt by ID
    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceipt(@PathVariable Long id) {
        try {
            Receipt receipt = service.getReceiptById(id);
            return ResponseEntity.ok(receipt);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Get all receipts
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts() {
        List<Receipt> receipts = service.getAllReceipts();
        return ResponseEntity.ok(receipts);
    }
    
    // Delete receipt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        try {
            service.deleteReceipt(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // Reprocess receipt with AI (in case of failure)
    @PostMapping("/{id}/reprocess")
    public ResponseEntity<Receipt> reprocessReceipt(@PathVariable Long id) throws IOException {
        try {
            Receipt receipt = service.reprocessReceipt(id);
            return ResponseEntity.ok(receipt);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
