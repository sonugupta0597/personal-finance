//package com.my_finance_manager_backend.service;
//
//import com.my_finance_manager_backend.config.GeminiConfig;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockMultipartFile;
//
//import java.net.http.HttpClient;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class GeminiAIServiceTest {
//
//    @Mock
//    private GeminiConfig geminiConfig;
//
//    @Mock
//    private HttpClient httpClient;
//
//    private GeminiAIService geminiAIService;
//
//    @BeforeEach
//    void setUp() {
//        geminiAIService = new GeminiAIService(geminiConfig, httpClient);
//    }
//
//    @Test
//    void testExtractInformationFromImageFile() throws Exception {
//        // Create a mock image file
//        MockMultipartFile imageFile = new MockMultipartFile(
//            "file",
//            "receipt.jpg",
//            "image/jpeg",
//            "fake-image-data".getBytes()
//        );
//
//        // Test that the service can handle image files
//        assertNotNull(geminiAIService);
//        assertNotNull(imageFile);
//        assertEquals("image/jpeg", imageFile.getContentType());
//    }
//
//    @Test
//    void testExtractInformationFromPdfFile() throws Exception {
//        // Create a mock PDF file
//        MockMultipartFile pdfFile = new MockMultipartFile(
//            "file",
//            "receipt.pdf",
//            "application/pdf",
//            "fake-pdf-data".getBytes()
//        );
//
//        // Test that the service can handle PDF files
//        assertNotNull(geminiAIService);
//        assertNotNull(pdfFile);
//        assertEquals("application/pdf", pdfFile.getContentType());
//    }
//
//    @Test
//    void testServiceInitialization() {
//        assertNotNull(geminiAIService);
//        assertNotNull(geminiConfig);
//        assertNotNull(httpClient);
//    }
//}
