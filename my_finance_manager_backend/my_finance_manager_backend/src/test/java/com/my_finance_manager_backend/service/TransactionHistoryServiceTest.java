//package com.my_finance_manager_backend.service;
//
//import com.my_finance_manager_backend.dto.TransactionDTO;
//import com.my_finance_manager_backend.model.Expense;
//import com.my_finance_manager_backend.model.Income;
//import com.my_finance_manager_backend.repository.ExpenseRepository;
//import com.my_finance_manager_backend.repository.IncomeRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class TransactionHistoryServiceTest {
//
//    @Mock
//    private ExpenseRepository expenseRepo;
//
//    @Mock
//    private IncomeRepository incomeRepo;
//
//    @Mock
//    private GeminiAIService geminiAIService;
//
//    private TransactionHistoryService transactionHistoryService;
//
//    @BeforeEach
//    void setUp() {
//        transactionHistoryService = new TransactionHistoryService(expenseRepo, incomeRepo, geminiAIService);
//    }
//
//    @Test
//    void testExtractTransactionsWithGeminiAI() throws Exception {
//        // Create a mock PDF file
//        MockMultipartFile file = new MockMultipartFile(
//            "file",
//            "test.pdf",
//            "application/pdf",
//            "Test PDF content".getBytes()
//        );
//
//        // Mock Gemini AI response
//        Map<String, Object> aiResult = Map.of(
//            "extractedText", "Sample extracted text from PDF",
//            "merchantName", "Test Merchant",
//            "amount", 100.0
//        );
//
//        when(geminiAIService.extractInformationFromFile(any())).thenReturn(aiResult);
//
//        // Test transaction extraction
//        List<TransactionDTO> transactions = transactionHistoryService.extractTransactions(file);
//
//        // Verify that the service attempted to use Gemini AI
//        verify(geminiAIService).extractInformationFromFile(file);
//
//        // Note: In a real scenario, this would return actual transactions
//        // For now, we're testing that the integration works without errors
//        assertNotNull(transactions);
//    }
//
//    @Test
//    void testSaveTransactions() {
//        // Create test transactions
//        List<TransactionDTO> transactions = List.of(
//            new TransactionDTO(LocalDate.now(), "Test Income", BigDecimal.valueOf(100), "INCOME"),
//            new TransactionDTO(LocalDate.now(), "Test Expense", BigDecimal.valueOf(50), "EXPENSE")
//        );
//
//        // Mock repository responses
//        when(incomeRepo.save(any(Income.class))).thenReturn(new Income());
//        when(expenseRepo.save(any(Expense.class))).thenReturn(new Expense());
//
//        // Test saving transactions
//        assertDoesNotThrow(() -> transactionHistoryService.saveTransactions(transactions));
//
//        // Verify that repositories were called
//        verify(incomeRepo).save(any(Income.class));
//        verify(expenseRepo).save(any(Expense.class));
//    }
//
//    @Test
//    void testListTransactions() {
//        // Mock repository responses
//        when(expenseRepo.findAll()).thenReturn(List.of());
//        when(incomeRepo.findAll()).thenReturn(List.of());
//
//        // Test listing transactions
//        var result = transactionHistoryService.listTransactions(
//            LocalDate.now().minusDays(30),
//            LocalDate.now(),
//            org.springframework.data.domain.PageRequest.of(0, 10)
//        );
//
//        assertNotNull(result);
//        assertEquals(0, result.getTotalElements());
//    }
//
//    @Test
//    void testGetSummary() {
//        // Mock repository responses
//        when(expenseRepo.findAll()).thenReturn(List.of());
//        when(incomeRepo.findAll()).thenReturn(List.of());
//
//        // Test getting summary
//        var summary = transactionHistoryService.getSummary(
//            LocalDate.now().minusDays(30),
//            LocalDate.now()
//        );
//
//        assertNotNull(summary);
//        assertEquals(BigDecimal.ZERO, summary.getTotalIncome());
//        assertEquals(BigDecimal.ZERO, summary.getTotalExpense());
//        assertEquals(BigDecimal.ZERO, summary.getNetBalance());
//    }
//
//    @Test
//    void testExtractTransactionsWithEmptyFile() {
//        // Test with empty file
//        MockMultipartFile emptyFile = new MockMultipartFile(
//            "file",
//            "empty.pdf",
//            "application/pdf",
//            new byte[0]
//        );
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            transactionHistoryService.extractTransactions(emptyFile);
//        });
//    }
//
//    @Test
//    void testExtractTransactionsWithLargeFile() {
//        // Test with file exceeding size limit
//        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
//        MockMultipartFile largeFile = new MockMultipartFile(
//            "file",
//            "large.pdf",
//            "application/pdf",
//            largeContent
//        );
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            transactionHistoryService.extractTransactions(largeFile);
//        });
//    }
//
//    @Test
//    void testExtractTransactionsWithUnsupportedFileType() {
//        // Test with unsupported file type
//        MockMultipartFile unsupportedFile = new MockMultipartFile(
//            "file",
//            "document.txt",
//            "text/plain",
//            "Some text content".getBytes()
//        );
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            transactionHistoryService.extractTransactions(unsupportedFile);
//        });
//    }
//
//    @Test
//    void testExtractTransactionsWithValidPDF() throws Exception {
//        // Test with valid PDF content
//        MockMultipartFile validFile = new MockMultipartFile(
//            "file",
//            "statement.pdf",
//            "application/pdf",
//            "Sample PDF content with transactions".getBytes()
//        );
//
//        // Mock Gemini AI response
//        Map<String, Object> aiResult = Map.of(
//            "extractedText", "Sample extracted text from PDF",
//            "merchantName", "Test Merchant",
//            "amount", 100.0
//        );
//
//        when(geminiAIService.extractInformationFromFile(any())).thenReturn(aiResult);
//
//        // Test transaction extraction
//        List<TransactionDTO> transactions = transactionHistoryService.extractTransactions(validFile);
//
//        // Verify that the service attempted to use Gemini AI
//        verify(geminiAIService).extractInformationFromFile(validFile);
//
//        // Should return some result (either from AI or fallback)
//        assertNotNull(transactions);
//    }
//}
