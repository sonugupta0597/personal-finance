package com.my_finance_manager_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my_finance_manager_backend.dto.TransactionDTO;
import com.my_finance_manager_backend.model.Expense;
import com.my_finance_manager_backend.model.Income;
import com.my_finance_manager_backend.repository.ExpenseRepository;
import com.my_finance_manager_backend.repository.IncomeRepository;
import com.my_finance_manager_backend.service.GeminiAIService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.my_finance_manager_backend.dto.TransactionViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.my_finance_manager_backend.dto.TransactionSummaryDTO;

import java.util.stream.Collectors;

import java.util.HashMap;
import java.util.Map;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.*;

@Service
public class TransactionHistoryService {

    private final ExpenseRepository expenseRepo;
    private final IncomeRepository incomeRepo;
    private final GeminiAIService geminiAIService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TransactionHistoryService(ExpenseRepository expenseRepo, IncomeRepository incomeRepo, GeminiAIService geminiAIService) {
        this.expenseRepo = expenseRepo;
        this.incomeRepo = incomeRepo;
        this.geminiAIService = geminiAIService;
    }

    // Step 1: Extract transactions from PDF using Gemini AI
    public List<TransactionDTO> extractTransactions(MultipartFile file) throws Exception {
        // Validate file first
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        
        // Check file type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("application/pdf") && !contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Unsupported file type. Only PDF and image files are supported.");
        }
        
        try {
            // Use Gemini AI to extract transactions from the PDF
            String aiResponse = callGeminiAPIForTransactions(file);
            List<TransactionDTO> transactions = parseAIResponseForTransactions(aiResponse);
            
            // If AI extraction fails, try fallback
            if (transactions.isEmpty()) {
                transactions = extractTransactionsFallback(file);
            }
            
            return transactions;
        } catch (Exception e) {
            // Log the error for debugging
            System.err.println("Error extracting transactions: " + e.getMessage());
            e.printStackTrace();
            
            // Try fallback extraction
            try {
                return extractTransactionsFallback(file);
            } catch (Exception fallbackError) {
                System.err.println("Fallback extraction also failed: " + fallbackError.getMessage());
                throw new Exception("Failed to extract transactions from file: " + e.getMessage(), e);
            }
        }
    }

    // Step 2: Save extracted transactions to DB
    public void saveTransactions(List<TransactionDTO> transactions) {
        for (TransactionDTO t : transactions) {
            if ("INCOME".equals(t.getType())) {
                incomeRepo.save(Income.builder()
                        .amount(t.getAmount())
                        .source(t.getDescription())
                        .description("Imported from PDF")
                        .date(t.getDate())
                        .build());
            } else {
                expenseRepo.save(Expense.builder()
                        .amount(t.getAmount())
                        .category("Imported")
                        .description(t.getDescription())
                        .date(t.getDate())
                        .build());
            }
        }
    }

    public Page<TransactionViewDTO> listTransactions(LocalDate start, LocalDate end, Pageable pageable) {
        // Get expenses
        List<TransactionViewDTO> expenses = expenseRepo.findAll().stream()
                .filter(e -> (start == null || !e.getDate().isBefore(start)) &&
                        (end == null || !e.getDate().isAfter(end)))
                .map(e -> new TransactionViewDTO(e.getDate(), e.getDescription(), e.getAmount(), "EXPENSE"))
                .collect(Collectors.toList());

        // Get incomes
        List<TransactionViewDTO> incomes = incomeRepo.findAll().stream()
                .filter(i -> (start == null || !i.getDate().isBefore(start)) &&
                        (end == null || !i.getDate().isAfter(end)))
                .map(i -> new TransactionViewDTO(i.getDate(), i.getSource(), i.getAmount(), "INCOME"))
                .collect(Collectors.toList());

        // Combine
        List<TransactionViewDTO> all = new ArrayList<>();
        all.addAll(expenses);
        all.addAll(incomes);

        // Sort by date (newest first)
        all.sort(Comparator.comparing(TransactionViewDTO::getDate).reversed());

        // Manual pagination
        int startIdx = (int) pageable.getOffset();
        int endIdx = Math.min((startIdx + pageable.getPageSize()), all.size());
        List<TransactionViewDTO> pageContent = all.subList(startIdx, endIdx);

        return new PageImpl<>(pageContent, pageable, all.size());
    }

    public TransactionSummaryDTO getSummary(LocalDate start, LocalDate end) {
        // ---- Income ----
        List<com.my_finance_manager_backend.model.Income> incomes = incomeRepo.findAll().stream()
                .filter(i -> (start == null || !i.getDate().isBefore(start)) &&
                        (end == null || !i.getDate().isAfter(end)))
                .collect(Collectors.toList());

        BigDecimal totalIncome = incomes.stream()
                .map(i -> i.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> incomeBySource = incomes.stream()
                .collect(Collectors.groupingBy(
                        i -> i.getSource(),
                        Collectors.reducing(BigDecimal.ZERO, i -> i.getAmount(), BigDecimal::add)
                ));

        // ---- Expense ----
        List<com.my_finance_manager_backend.model.Expense> expenses = expenseRepo.findAll().stream()
                .filter(e -> (start == null || !e.getDate().isBefore(start)) &&
                        (end == null || !e.getDate().isAfter(end)))
                .collect(Collectors.toList());

        BigDecimal totalExpense = expenses.stream()
                .map(e -> e.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> expenseByCategory = expenses.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getCategory(),
                        Collectors.reducing(BigDecimal.ZERO, e -> e.getAmount(), BigDecimal::add)
                ));

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        return TransactionSummaryDTO.builder()
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .incomeBySource(incomeBySource)
                .expenseByCategory(expenseByCategory)
                .build();
    }
    
    // Helper methods for PDF processing with Gemini AI
    private String callGeminiAPIForTransactions(MultipartFile file) throws Exception {
        try {
            // Use Gemini AI to extract information from the file
            Map<String, Object> extractedInfo = geminiAIService.extractInformationFromFile(file);
            
            String extractedText = (String) extractedInfo.get("extractedText");
            if (extractedText == null || extractedText.trim().isEmpty()) {
                System.err.println("No text extracted from file");
                return "[]";
            }
            
            System.out.println("File text extracted successfully, length: " + extractedText.length());
            System.out.println("First 500 characters: " + extractedText.substring(0, Math.min(500, extractedText.length())));
            
            // Use pattern-based extraction for transactions
            List<TransactionDTO> transactions = extractTransactionsFromText(extractedText);
            
            // Convert transactions back to JSON for consistency
            return objectMapper.writeValueAsString(transactions);
            
        } catch (Exception e) {
            throw new Exception("Failed to process file with Gemini AI: " + e.getMessage(), e);
        }
    }
    
    private List<TransactionDTO> extractTransactionsFromText(String text) {
        List<TransactionDTO> transactions = new ArrayList<>();
        
        try {
            // Split text into lines for processing
            String[] lines = text.split("\\n");
            
            for (String line : lines) {
                line = line.trim();
                if (line.length() < 10) continue; // Skip very short lines
                
                // Try to extract transaction from this line
                TransactionDTO transaction = extractTransactionFromLine(line);
                if (transaction != null) {
                    transactions.add(transaction);
                }
            }
            
            System.out.println("Extracted " + transactions.size() + " transactions from PDF text");
            
        } catch (Exception e) {
            System.err.println("Error extracting transactions from text: " + e.getMessage());
        }
        
        return transactions;
    }
    
    private TransactionDTO extractTransactionFromLine(String line) {
        try {
            // Look for date patterns first
            LocalDate date = extractDateFromLine(line);
            if (date == null) return null;
            
            // Look for amount patterns
            BigDecimal amount = extractAmountFromLine(line);
            if (amount == null) return null;
            
            // Extract description
            String description = extractDescriptionFromLine(line);
            
            // Determine transaction type based on amount
            String type = amount.signum() >= 0 ? "EXPENSE" : "INCOME";
            
            return new TransactionDTO(date, description, amount.abs(), type, null);
            
        } catch (Exception e) {
            System.err.println("Failed to extract transaction from line: " + line + ", Error: " + e.getMessage());
            return null;
        }
    }
    
    private LocalDate extractDateFromLine(String line) {
        try {
            // Multiple date patterns
            Pattern[] datePatterns = {
                Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b"), // YYYY-MM-DD
                Pattern.compile("\\b(\\d{1,2}/\\d{1,2}/\\d{2,4})\\b"), // MM/DD/YYYY
                Pattern.compile("\\b(\\d{1,2}\\s+(Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+\\d{2,4})\\b", Pattern.CASE_INSENSITIVE) // DD Month YYYY
            };
            
            for (Pattern pattern : datePatterns) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String dateStr = matcher.group(1);
                    return parseDate(dateStr);
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting date from line: " + e.getMessage());
        }
        return null;
    }
    
    private BigDecimal extractAmountFromLine(String line) {
        try {
            // Look for currency patterns first
            Pattern currencyPattern = Pattern.compile("\\$\\s*([+-]?\\d+(\\.\\d{2})?)");
            Matcher currencyMatcher = currencyPattern.matcher(line);
            
            if (currencyMatcher.find()) {
                String amountStr = currencyMatcher.group(1);
                return new BigDecimal(amountStr);
            }
            
            // Look for any number that could be an amount
            Pattern amountPattern = Pattern.compile("\\b([+-]?\\d+(\\.\\d{2})?)\\b");
            Matcher amountMatcher = amountPattern.matcher(line);
            
            while (amountMatcher.find()) {
                String amountStr = amountMatcher.group(1);
                BigDecimal amount = new BigDecimal(amountStr);
                if (amount.abs().compareTo(BigDecimal.ZERO) > 0 && amount.abs().compareTo(new BigDecimal("10000")) < 0) {
                    return amount;
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting amount from line: " + e.getMessage());
        }
        return null;
    }
    
    private String extractDescriptionFromLine(String line) {
        try {
            // Remove date and amount patterns to get description
            String description = line
                .replaceAll("\\b\\d{4}-\\d{2}-\\d{2}\\b", "") // Remove YYYY-MM-DD
                .replaceAll("\\b\\d{1,2}[/\\\\]\\d{1,2}[/\\\\]\\d{2,4}\\b", "") // Remove MM/DD/YYYY
                .replaceAll("\\$\\s*[+-]?\\d+(\\.\\d{2})?", "") // Remove amounts
                .replaceAll("\\b[+-]?\\d+(\\.\\d{2})?\\b", "") // Remove other numbers
                .replaceAll("\\s+", " ") // Normalize whitespace
                .trim();
            
            if (description.length() > 5 && description.length() < 100) {
                return description;
            }
        } catch (Exception e) {
            System.err.println("Error extracting description from line: " + e.getMessage());
        }
        return "Transaction";
    }
    
    private List<TransactionDTO> parseAIResponseForTransactions(String aiResponse) throws Exception {
        try {
            List<TransactionDTO> transactions = new ArrayList<>();
            
            JsonNode responseNode = objectMapper.readTree(aiResponse);
            JsonNode candidates = responseNode.get("candidates");
            
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.get(0);
                JsonNode content = firstCandidate.get("content");
                
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode firstPart = parts.get(0);
                        String text = firstPart.get("text").asText();
                        
                        // Try to extract JSON array from the response
                        List<TransactionDTO> extractedTransactions = extractTransactionsFromJSON(text);
                        if (!extractedTransactions.isEmpty()) {
                            return extractedTransactions;
                        }
                        
                        // If JSON extraction fails, try pattern matching
                        return extractTransactionsUsingPatterns(text);
                    }
                }
            }
            
            return transactions;
        } catch (Exception e) {
            throw new Exception("Failed to parse AI response for transactions: " + e.getMessage(), e);
        }
    }
    
    private List<TransactionDTO> extractTransactionsFromJSON(String text) {
        List<TransactionDTO> transactions = new ArrayList<>();
        
        try {
            // Try to find JSON array in the text
            int startIndex = text.indexOf("[");
            int endIndex = text.lastIndexOf("]");
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                String jsonArrayText = text.substring(startIndex, endIndex + 1);
                JsonNode jsonArray = objectMapper.readTree(jsonArrayText);
                
                if (jsonArray.isArray()) {
                    for (JsonNode transactionNode : jsonArray) {
                        try {
                            String dateStr = transactionNode.get("date").asText();
                            String description = transactionNode.get("description").asText();
                            BigDecimal amount = new BigDecimal(transactionNode.get("amount").asText());
                            String type = transactionNode.get("type").asText();
                            
                            LocalDate date = LocalDate.parse(dateStr);
                            transactions.add(new TransactionDTO(date, description, amount, type, null));
                        } catch (Exception e) {
                            // Skip invalid transaction entries
                        }
                    }
                }
            }
        } catch (Exception e) {
            // If JSON parsing fails, return empty list
        }
        
        return transactions;
    }
    
    private List<TransactionDTO> extractTransactionsFallback(MultipartFile file) throws Exception {
        // Fallback to basic text extraction using Tika
        try {
            org.apache.tika.Tika tika = new org.apache.tika.Tika();
            String text = tika.parseToString(file.getInputStream());
            
            if (text == null || text.trim().isEmpty()) {
                System.err.println("Tika extracted empty text from file");
                return new ArrayList<>();
            }
            
            System.out.println("Extracted text from PDF (first 500 chars): " + text.substring(0, Math.min(500, text.length())));
            
            List<TransactionDTO> transactions = extractTransactionsUsingPatterns(text);
            
            if (transactions.isEmpty()) {
                System.err.println("No transactions found using pattern matching");
                // Try to find any numeric patterns that might be transactions
                transactions = extractBasicTransactionPatterns(text);
            }
            
            return transactions;
        } catch (Exception e) {
            System.err.println("Fallback extraction failed: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private List<TransactionDTO> extractBasicTransactionPatterns(String text) {
        List<TransactionDTO> transactions = new ArrayList<>();
        
        try {
            // Look for basic patterns like dates followed by amounts
            // Pattern: Date (various formats) followed by description and amount
            Pattern basicPattern = Pattern.compile(
                    "(\\d{1,2}/\\d{1,2}/\\d{2,4}|\\d{4}-\\d{2}-\\d{2})\\s+" +    // Date
                            "(.+?)\\s+" +                                                // Description (non-greedy)
                            "([+-]?\\d+(?:[.,]\\d{1,2})?)"                               // Amount
            );


            Matcher matcher = basicPattern.matcher(text);
            while (matcher.find()) {
                try {
                    String dateStr = matcher.group(1);
                    String description = matcher.group(2).trim();
                    String amountStr = matcher.group(3).replace(",", ".");
                    
                    // Parse date
                    LocalDate date = parseDate(dateStr);
                    if (date != null) {
                        // Parse amount
                        BigDecimal amount = new BigDecimal(amountStr);
                        
                        // Determine type based on amount sign
                        String type = amount.signum() >= 0 ? "EXPENSE" : "INCOME";
                        
                        // Clean up description
                        description = description.replaceAll("[\s\n\r]+", " ").trim();
                        if (description.length() > 100) {
                            description = description.substring(0, 100) + "...";
                        }
                        
                        transactions.add(new TransactionDTO(date, description, amount.abs(), type, null));
                    }
                } catch (Exception e) {
                    // Skip invalid matches
                    System.err.println("Failed to parse basic pattern match: " + e.getMessage());
                }
            }
            
            System.out.println("Basic pattern extraction found " + transactions.size() + " transactions");
            
        } catch (Exception e) {
            System.err.println("Basic pattern extraction failed: " + e.getMessage());
        }
        
        return transactions;
    }
    
    private List<TransactionDTO> extractTransactionsUsingPatterns(String text) {
        List<TransactionDTO> transactions = new ArrayList<>();
        
        // Multiple regex patterns for different statement formats
        List<Pattern> patterns = Arrays.asList(
                Pattern.compile("(\\d{4}-\\d{2}-\\d{2})\\s+(.+?)\\s+([+-]?\\d+\\.\\d{2})"),  // YYYY-MM-DD Description Amount

                Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4})\\s+(.+?)\\s+([+-]?\\d+\\.\\d{2})"),  // MM/DD/YYYY Description Amount

                Pattern.compile("(\\d{1,2}-\\d{1,2}-\\d{4})\\s+(.+?)\\s+([+-]?\\d+\\.\\d{2})"),  // DD-MM-YYYY Description Amount

                Pattern.compile("(.+?)\\s+([+-]?\\d+\\.\\d{2})\\s+(\\d{1,2}/\\d{1,2}/\\d{4})")   // Description Amount Date

        );
        
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                try {
                    String dateStr = matcher.group(1);
                    String description = matcher.group(2).trim();
                    BigDecimal amount = new BigDecimal(matcher.group(3));
                    
                    // Determine date format and parse
                    LocalDate date = parseDate(dateStr);
                    if (date != null) {
                        String type = amount.signum() >= 0 ? "INCOME" : "EXPENSE";
                        transactions.add(new TransactionDTO(date, description, amount.abs(), type, null));
                    }
                } catch (Exception e) {
                    // Skip invalid matches
                }
            }
        }
        
        return transactions;
    }
    
    private LocalDate parseDate(String dateStr) {
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            // try another format
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        } catch (Exception e) {
            // try another format
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } catch (Exception e) {
            // try another format
        }
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        } catch (Exception e) {
            // Return null if parsing fails
        }
        return null;
    }

    public TransactionDTO saveTransaction(TransactionDTO transactionDTO) {
        if ("INCOME".equalsIgnoreCase(transactionDTO.getType())) {
            Income income = Income.builder()
                    .amount(transactionDTO.getAmount())
                    .source(transactionDTO.getDescription())
                    .description("Imported from receipt")
                    .date(transactionDTO.getDate())
                    .build();
            Income savedIncome = incomeRepo.save(income);
            return new TransactionDTO(savedIncome.getDate(), savedIncome.getSource(), savedIncome.getAmount(), "INCOME", null);
        } else {
            Expense expense = Expense.builder()
                    .amount(transactionDTO.getAmount())
                    .category(transactionDTO.getCategory())
                    .description(transactionDTO.getDescription())
                    .date(transactionDTO.getDate())
                    .build();
            Expense savedExpense = expenseRepo.save(expense);
            return new TransactionDTO(savedExpense.getDate(), savedExpense.getDescription(), savedExpense.getAmount(), "EXPENSE", savedExpense.getCategory());
        }
    }
}
