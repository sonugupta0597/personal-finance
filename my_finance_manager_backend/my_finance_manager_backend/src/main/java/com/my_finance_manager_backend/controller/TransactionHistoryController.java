package com.my_finance_manager_backend.controller;

import com.my_finance_manager_backend.dto.TransactionDTO;
import com.my_finance_manager_backend.service.TransactionHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.my_finance_manager_backend.dto.TransactionViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import com.my_finance_manager_backend.dto.TransactionSummaryDTO;


import java.time.LocalDate;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
public class TransactionHistoryController {

    private final TransactionHistoryService service;

    public TransactionHistoryController(TransactionHistoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(@RequestBody TransactionDTO transactionDTO) {
        try {
            TransactionDTO savedTransaction = service.saveTransaction(transactionDTO);
            return ResponseEntity.ok(savedTransaction);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    

    // Extract transactions from PDF/Statement
    @PostMapping("/extract")
    public ResponseEntity<List<TransactionDTO>> extractTransactions(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            List<TransactionDTO> transactions = service.extractTransactions(file);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    // Alternative endpoint for backward compatibility
    @PostMapping("/upload")
    public ResponseEntity<List<TransactionDTO>> uploadStatement(@RequestParam("file") MultipartFile file) throws Exception {
        return extractTransactions(file);
    }

    // Save extracted transactions to DB
    @PostMapping("/save")
    public ResponseEntity<String> saveTransactions(@RequestBody List<TransactionDTO> transactions) {
        try {
            service.saveTransactions(transactions);
            return ResponseEntity.ok("Transactions saved successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to save transactions: " + e.getMessage());
        }
    }

    // Upload PDF and save transactions in one step
    @PostMapping("/upload-and-save")
    public ResponseEntity<String> uploadAndSaveTransactions(@RequestParam("file") MultipartFile file) throws Exception {
        try {
            // Extract transactions
            List<TransactionDTO> transactions = service.extractTransactions(file);
            
            if (transactions.isEmpty()) {
                return ResponseEntity.badRequest().body("No transactions extracted from the PDF. Please check the file format.");
            }
            
            // Save transactions
            service.saveTransactions(transactions);
            
            return ResponseEntity.ok("Successfully extracted and saved " + transactions.size() + " transactions!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to process PDF: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Page<TransactionViewDTO>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(service.listTransactions(startDate, endDate, pageable));
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryDTO> getTransactionSummary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(service.getSummary(startDate, endDate));
    }
}
