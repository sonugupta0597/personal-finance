//package com.my_finance_manager_backend.service;
//
//import com.my_finance_manager_backend.model.Receipt;
//import com.my_finance_manager_backend.repository.ReceiptRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ReceiptServiceTest {
//
//    @Mock
//    private ReceiptRepository receiptRepo;
//
//    @Mock
//    private GeminiAIService geminiAIService;
//
//    private ReceiptService receiptService;
//
//    @BeforeEach
//    void setUp() {
//        receiptService = new ReceiptService(receiptRepo, geminiAIService);
//    }
//
//    @Test
//    void testSaveReceiptWithValidFile() throws IOException, InterruptedException {
//        // Create a mock receipt image file
//        MockMultipartFile file = new MockMultipartFile(
//            "file",
//            "receipt.jpg",
//            "image/jpeg",
//            "Sample receipt image content".getBytes()
//        );
//
//        // Mock Gemini AI response
//        Map<String, Object> aiResult = Map.of(
//            "extractedText", "Sample extracted text from receipt",
//            "merchantName", "Test Store",
//            "amount", 25.50,
//            "currency", "USD",
//            "transactionDate", "2024-01-15",
//            "category", "Shopping",
//            "description", "Grocery purchase",
//            "aiConfidence", "95%"
//        );
//
//        when(geminiAIService.extractInformationFromFile(any())).thenReturn(aiResult);
//        when(receiptRepo.save(any(Receipt.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Test receipt upload
//        Receipt receipt = receiptService.saveReceipt(file);
//
//        // Verify that the service attempted to use Gemini AI
//        verify(geminiAIService).extractInformationFromFile(file);
//        verify(receiptRepo).save(any(Receipt.class));
//
//        // Verify receipt properties
//        assertNotNull(receipt);
//        assertEquals("receipt.jpg", receipt.getFileName());
//        assertEquals("image/jpeg", receipt.getFileType());
//        assertTrue(receipt.isProcessed());
//        assertEquals("Test Store", receipt.getMerchantName());
//        assertEquals(25.50, receipt.getAmount());
//        assertEquals("USD", receipt.getCurrency());
//        assertEquals("Shopping", receipt.getCategory());
//    }
//
//    @Test
//    void testSaveReceiptWithEmptyFile() {
//        // Test with empty file
//        MockMultipartFile emptyFile = new MockMultipartFile(
//            "file",
//            "empty.jpg",
//            "image/jpeg",
//            new byte[0]
//        );
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            receiptService.saveReceipt(emptyFile);
//        });
//    }
//
//    @Test
//    void testSaveReceiptWithLargeFile() {
//        // Test with file exceeding size limit
//        byte[] largeContent = new byte[11 * 1024 * 1024]; // 11MB
//        MockMultipartFile largeFile = new MockMultipartFile(
//            "file",
//            "large.jpg",
//            "image/jpeg",
//            largeContent
//        );
//
//        assertThrows(IllegalArgumentException.class, () -> {
//            receiptService.saveReceipt(largeFile);
//        });
//    }
//
//    @Test
//    void testSaveReceiptWithAIProcessingFailure() throws IOException, InterruptedException {
//        // Create a mock file
//        MockMultipartFile file = new MockMultipartFile(
//            "file",
//            "receipt.jpg",
//            "image/jpeg",
//            "Sample content".getBytes()
//        );
//
//        // Mock AI service failure
//        when(geminiAIService.extractInformationFromFile(any())).thenThrow(new RuntimeException("AI service error"));
//        when(receiptRepo.save(any(Receipt.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        // Test receipt upload with AI failure
//        Receipt receipt = receiptService.saveReceipt(file);
//
//        // Verify receipt was saved despite AI failure
//        assertNotNull(receipt);
//        assertFalse(receipt.isProcessed());
//        assertTrue(receipt.getExtractedText().contains("AI processing failed"));
//
//        verify(receiptRepo).save(any(Receipt.class));
//    }
//
//    @Test
//    void testGetReceiptById() {
//        // Mock receipt
//        Receipt mockReceipt = Receipt.builder()
//            .id(1L)
//            .fileName("test.jpg")
//            .processed(true)
//            .build();
//
//        when(receiptRepo.findById(1L)).thenReturn(Optional.of(mockReceipt));
//
//        // Test getting receipt by ID
//        Receipt receipt = receiptService.getReceiptById(1L);
//
//        assertNotNull(receipt);
//        assertEquals(1L, receipt.getId());
//        assertEquals("test.jpg", receipt.getFileName());
//    }
//
//    @Test
//    void testGetReceiptByIdNotFound() {
//        when(receiptRepo.findById(999L)).thenReturn(Optional.empty());
//
//        assertThrows(RuntimeException.class, () -> {
//            receiptService.getReceiptById(999L);
//        });
//    }
//
//    @Test
//    void testGetAllReceipts() {
//        // Mock receipts
//        List<Receipt> mockReceipts = List.of(
//            Receipt.builder().id(1L).fileName("receipt1.jpg").build(),
//            Receipt.builder().id(2L).fileName("receipt2.jpg").build()
//        );
//
//        when(receiptRepo.findAll()).thenReturn(mockReceipts);
//
//        // Test getting all receipts
//        List<Receipt> receipts = receiptService.getAllReceipts();
//
//        assertNotNull(receipts);
//        assertEquals(2, receipts.size());
//        assertEquals("receipt1.jpg", receipts.get(0).getFileName());
//        assertEquals("receipt2.jpg", receipts.get(1).getFileName());
//    }
//
//    @Test
//    void testDeleteReceipt() {
//        // Test deleting receipt
//        receiptService.deleteReceipt(1L);
//
//        verify(receiptRepo).deleteById(1L);
//    }
//}
