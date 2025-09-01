

package com.my_finance_manager_backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.my_finance_manager_backend.config.GeminiConfig;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Base64;

@Service
public class GeminiAIService {

    private final GeminiConfig geminiConfig;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Tika tika;

    public GeminiAIService(GeminiConfig geminiConfig, HttpClient httpClient) {
        this.geminiConfig = geminiConfig;
        this.httpClient = httpClient;
        this.objectMapper = new ObjectMapper();
        this.tika = new Tika();
    }

    /**
     * Extract information from uploaded files (images or PDFs)
     */
    public Map<String, Object> extractInformationFromFile(MultipartFile file) throws IOException, InterruptedException {
        System.out.println("Processing file: " + file.getOriginalFilename() + " with Gemini AI");
        
        String contentType = file.getContentType();
        if (contentType != null && contentType.startsWith("image/")) {
            return processImageWithGeminiFlash(file);
        } else if (contentType != null && contentType.equals("application/pdf")) {
            return processPDFWithGeminiFlash(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + contentType);
        }
    }

    /**
     * Process images using Gemini 1.5 Flash API
     */
    private Map<String, Object> processImageWithGeminiFlash(MultipartFile file) throws IOException, InterruptedException {
        try {
            // Convert image to base64
            String base64Image = Base64.getEncoder().encodeToString(file.getBytes());
            
            // Create prompt for receipt analysis
            String prompt = createReceiptAnalysisPrompt();
            
            // Call Gemini 1.5 Flash API
            String aiResponse = callGeminiFlashVisionAPI(prompt, base64Image);
            
            // Parse AI response and extract information
            Map<String, Object> extractedInfo = parseAIResponse(aiResponse);
            
            // Add file metadata
            extractedInfo.put("fileName", file.getOriginalFilename());
            extractedInfo.put("fileType", file.getContentType());
            extractedInfo.put("fileSize", file.getSize());
            extractedInfo.put("processingMethod", "Gemini 1.5 Flash API");
            
            return extractedInfo;
            
        } catch (Exception e) {
            System.err.println("Gemini 1.5 Flash API processing failed: " + e.getMessage());
            // Fallback to pattern extraction
            return extractUsingPatterns(new String(file.getBytes()));
        }
    }

    /**
     * Process PDFs using Gemini 1.5 Flash API
     */
    private Map<String, Object> processPDFWithGeminiFlash(MultipartFile file) throws IOException, InterruptedException {
        try {
            // Extract text from PDF using Tika
            String pdfText = tika.parseToString(file.getInputStream());
            System.out.println("Extracted PDF text length: " + pdfText.length());
            
            if (pdfText == null || pdfText.trim().isEmpty()) {
                System.err.println("No text content extracted from PDF");
                return createDefaultResult(file);
            }
            
            // Create prompt for PDF analysis
            String prompt = createPDFAnalysisPrompt(pdfText);
            
            // Call Gemini 1.5 Flash API
            String aiResponse = callGeminiFlashTextAPI(prompt);
            
            // Parse AI response and extract information
            Map<String, Object> extractedInfo = parseAIResponse(aiResponse);
            
            // Add file metadata
            extractedInfo.put("fileName", file.getOriginalFilename());
            extractedInfo.put("fileType", file.getContentType());
            extractedInfo.put("fileSize", file.getSize());
            extractedInfo.put("processingMethod", "Gemini 1.5 Flash API");
            extractedInfo.put("extractedText", pdfText);
            
            return extractedInfo;
            
        } catch (TikaException e) {
            System.err.println("PDF text extraction failed: " + e.getMessage());
            return createDefaultResult(file);
        } catch (Exception e) {
            System.err.println("PDF processing failed: " + e.getMessage());
            return createDefaultResult(file);
        }
    }

    /**
     * Call Gemini 1.5 Flash API for image processing
     */
    private String callGeminiFlashVisionAPI(String prompt, String base64Image) throws IOException, InterruptedException {
        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + geminiConfig.getApiKey();
            
            // Create request body for Gemini 1.5 Flash
            Map<String, Object> requestBody = createGeminiFlashVisionRequest(prompt, base64Image);
            
            String requestJson = objectMapper.writeValueAsString(requestBody);
            System.out.println("Gemini 1.5 Flash API request JSON: " + requestJson);
            System.out.println("Request body structure: " + requestBody);
            System.out.println("Contents size: " + ((List<?>) requestBody.get("contents")).size());
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("Gemini 1.5 Flash API response received successfully");
                return response.body();
            } else {
                System.err.println("Gemini 1.5 Flash API call failed with status: " + response.statusCode() + ", Response: " + response.body());
                throw new RuntimeException("Gemini 1.5 Flash API call failed with status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Gemini 1.5 Flash API call failed: " + e.getMessage());
            throw new RuntimeException("Gemini 1.5 Flash API call failed", e);
        }
    }

    /**
     * Create request body for Gemini 1.5 Flash API (Text)
     */
    private Map<String, Object> createGeminiFlashTextRequest(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Create the content array
        List<Map<String, Object>> contents = new ArrayList<>();
        
        // Add text part
        Map<String, Object> textPart = new HashMap<>();
        textPart.put("role", "user");

        textPart.put("parts", new Object[]{
            Map.of("text", prompt)
        });
        contents.add(textPart);
        
        requestBody.put("contents", contents);
        return requestBody;
    }



    /**
     * Create request body for Gemini 1.5 Flash API (Vision)
     */
    private Map<String, Object> createGeminiFlashVisionRequest(String prompt, String base64Image) {
        Map<String, Object> requestBody = new HashMap<>();
        
        // Create the content array
        List<Map<String, Object>> contents = new ArrayList<>();
        
        // Add content part with text and image
        Map<String, Object> contentPart = new HashMap<>();
        contentPart.put("role", "user");
        contentPart.put("parts", new Object[]{
            Map.of("text", prompt),
            Map.of("inlineData", Map.of(
                "mimeType", "image/jpeg",
                "data", base64Image
            ))
        });
        contents.add(contentPart);
        
        requestBody.put("contents", contents);
        return requestBody;
    }

    /**
     * Call Gemini 1.5 Flash API for text processing
     */
    private String callGeminiFlashTextAPI(String prompt) throws IOException, InterruptedException {
        try {
            String endpoint = "https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent?key=" + geminiConfig.getApiKey();
            
            // Create request body for Gemini 1.5 Flash
            Map<String, Object> requestBody = createGeminiFlashTextRequest(prompt);
            
            String requestJson = objectMapper.writeValueAsString(requestBody);
            System.out.println("Gemini 1.5 Flash API request JSON: " + requestJson);
            System.out.println("Request body structure: " + requestBody);
            System.out.println("Contents size: " + ((List<?>) requestBody.get("contents")).size());
            
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                System.out.println("Gemini 1.5 Flash API response received successfully");
                return response.body();
            } else {
                System.err.println("Gemini 1.5 Flash API call failed with status: " + response.statusCode() + ", Response: " + response.body());
                throw new RuntimeException("Gemini 1.5 Flash API call failed with status: " + response.statusCode());
            }
            
        } catch (Exception e) {
            System.err.println("Gemini 1.5 Flash API call failed: " + e.getMessage());
            throw new RuntimeException("Gemini 1.5 Flash API call failed", e);
        }
    }

    /**
     * Create prompt for receipt analysis
     */
    private String createReceiptAnalysisPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze this receipt image and extract the following information in JSON format:\n");
        sb.append("{\n");
        sb.append("    \"merchantName\": \"business/company name\",\n");
        sb.append("    \"amount\": number,\n");
        sb.append("    \"currency\": \"currency code\",\n");
        sb.append("    \"transactionDate\": \"YYYY-MM-DD\",\n");
        sb.append("    \"category\": \"category name\",\n");
        sb.append("    \"description\": \"transaction description\",\n");
        sb.append("    \"invoiceNumber\": \"invoice/receipt number\",\n");
        sb.append("    \"taxAmount\": number,\n");
        sb.append("    \"totalAmount\": number,\n");
        sb.append("    \"paymentMethod\": \"payment method used\",\n");
        sb.append("    \"items\": [\"list of items purchased\"]\n");
        sb.append("}\n\n");
        sb.append("For the category, classify it as one of: Food & Dining, Transportation, Shopping, Entertainment, Healthcare, Utilities, Education, Business, Travel, or Other.\n\n");
        sb.append("Return only valid JSON with these exact field names. If you can't find a field, use empty string \"\" for text or 0.0 for numbers.");
        return sb.toString();
    }

    /**
     * Create prompt for PDF analysis
     */
    private String createPDFAnalysisPrompt(String pdfText) {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze this PDF document (bill, invoice, or financial statement) and extract the following information in JSON format:\n");
        sb.append("{\n");
        sb.append("    \"merchantName\": \"business/company name\",\n");
        sb.append("    \"amount\": number,\n");
        sb.append("    \"currency\": \"currency code\",\n");
        sb.append("    \"transactionDate\": \"YYYY-MM-DD\",\n");
        sb.append("    \"category\": \"category name\",\n");
        sb.append("    \"description\": \"transaction description\",\n");
        sb.append("    \"invoiceNumber\": \"invoice/receipt number\",\n");
        sb.append("    \"taxAmount\": number,\n");
        sb.append("    \"totalAmount\": number,\n");
        sb.append("    \"paymentMethod\": \"payment method used\",\n");
        sb.append("    \"items\": [\"list of items purchased\"]\n");
        sb.append("}\n\n");
        sb.append("For the category, classify it as one of: Food & Dining, Transportation, Shopping, Entertainment, Healthcare, Utilities, Education, Business, Travel, or Other.\n\n");
        sb.append("Return only valid JSON with these exact field names. If you can't find a field, use empty string \"\" for text or 0.0 for numbers.\n\n");
        sb.append("Document content:\n");
        sb.append(pdfText);
        return sb.toString();
    }

    /**
     * Parse AI response from Gemini API
     */
    private Map<String, Object> parseAIResponse(String aiResponse) throws IOException {
        Map<String, Object> result = new HashMap<>();
        
        try {
            JsonNode responseNode = objectMapper.readTree(aiResponse);
            JsonNode candidates = responseNode.path("candidates");
            
            if (candidates != null && candidates.isArray() && candidates.size() > 0) {
                JsonNode firstCandidate = candidates.path(0);
                JsonNode content = firstCandidate.get("content");
                
                if (content != null) {
                    JsonNode parts = content.get("parts");
                    if (parts != null && parts.isArray() && parts.size() > 0) {
                        JsonNode firstPart = parts.get(0);
                        String text = firstPart.get("text").asText();
                        
                        // Try to extract JSON from the response
                        Map<String, Object> extractedData = extractJSONFromText(text);
                        
                        if (!extractedData.isEmpty()) {
                            result.put("extractedText", text);
                            result.put("merchantName", extractedData.getOrDefault("merchantName", ""));
                            result.put("amount", extractedData.getOrDefault("amount", 0.0));
                            result.put("currency", extractedData.getOrDefault("currency", ""));
                            result.put("transactionDate", extractedData.getOrDefault("transactionDate", ""));
                            result.put("category", extractedData.getOrDefault("category", ""));
                            result.put("description", extractedData.getOrDefault("description", ""));
                            result.put("invoiceNumber", extractedData.getOrDefault("invoiceNumber", ""));
                            result.put("taxAmount", extractedData.getOrDefault("taxAmount", 0.0));
                            result.put("totalAmount", extractedData.getOrDefault("totalAmount", 0.0));
                            result.put("paymentMethod", extractedData.getOrDefault("paymentMethod", ""));
                            result.put("items", extractedData.getOrDefault("items", new String[0]));
                            result.put("aiConfidence", "95%");
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            // If parsing fails, use pattern extraction on the raw text
            System.err.println("AI response parsing failed, using pattern extraction: " + e.getMessage());
            Map<String, Object> fallbackResult = extractUsingPatterns(aiResponse);
            result.putAll(fallbackResult);
            result.put("extractedText", aiResponse); // Keep raw AI response for debugging
            result.put("aiConfidence", "0% (Parsing Failed)");
        }
        
        return result;
    }

    /**
     * Extract JSON from AI response text
     */
    private Map<String, Object> extractJSONFromText(String text) {
        Map<String, Object> result = new HashMap<>(); // Initialize with default values
        
        try {
            // Try to find JSON in the text
            int startIndex = text.indexOf("{");
            int endIndex = text.lastIndexOf("}");
            
            if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                String jsonText = text.substring(startIndex, endIndex + 1);
                JsonNode jsonNode = objectMapper.readTree(jsonText);
                
                result.put("merchantName", getStringValue(jsonNode, "merchantName"));
                result.put("amount", getDoubleValue(jsonNode, "amount"));
                result.put("currency", getStringValue(jsonNode, "currency"));
                result.put("transactionDate", getStringValue(jsonNode, "transactionDate"));
                result.put("category", getStringValue(jsonNode, "category"));
                result.put("description", getStringValue(jsonNode, "description"));
                result.put("invoiceNumber", getStringValue(jsonNode, "invoiceNumber"));
                result.put("taxAmount", getDoubleValue(jsonNode, "taxAmount"));
                result.put("totalAmount", getDoubleValue(jsonNode, "totalAmount"));
                result.put("paymentMethod", getStringValue(jsonNode, "paymentMethod"));
                result.put("items", getArrayValue(jsonNode, "items"));
            }
        } catch (Exception e) {
            // If JSON parsing fails, use pattern extraction
            System.err.println("JSON extraction failed: " + e.getMessage());
        }
        
        return result;
    }

    private String getStringValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        return field != null ? field.asText() : "";
    }

    private double getDoubleValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field != null && field.isNumber()) {
            return field.asDouble();
        }
        return 0.0;
    }

    private Object getArrayValue(JsonNode node, String fieldName) {
        JsonNode field = node.get(fieldName);
        if (field != null && field.isArray()) {
            try {
                return objectMapper.convertValue(field, String[].class);
            } catch (Exception e) {
                return new String[0];
            }
        }
        return new String[0];
    }

    /**
     * Extract information using pattern matching (fallback method)
     */
    private Map<String, Object> extractUsingPatterns(String text) {
        Map<String, Object> result = new HashMap<>();
        
        if (text == null || text.trim().isEmpty()) {
            System.err.println("No text content available for pattern extraction");
            setDefaultValues(result);
            result.put("extractedText", text); // Store original text even if empty
            result.put("aiConfidence", "0% (No content)");
            return result;
        }
        
        System.out.println("Performing pattern-based extraction on text...");
        
        // Extract amount (look for currency patterns)
        extractAmount(text, result); // Now a separate method
        
        // Extract date patterns
        extractDate(text, result); // Now a separate method
        
        // Extract merchant name
        extractMerchantName(text, result); // Now a separate method
        
        // Extract category based on keywords
        extractCategory(text, result); // Now a separate method
        
        // Extract description
        extractDescription(text, result); // Now a separate method

        // Extract invoice number
        extractInvoiceNumber(text, result);

        // Extract tax amount
        extractTaxAmount(text, result);
        
        // Set default values for missing fields
        setDefaultValues(result); // Now a separate method
        
        System.out.println("Pattern extraction completed: " + result);
        return result;
    }

    private void extractAmount(String text, Map<String, Object> result) {
        // Look for currency patterns first
        Pattern currencyPattern = Pattern.compile("\\$\\s*([+-]?\\d+(?:\\.\\d{2})?)");
        Matcher currencyMatcher = currencyPattern.matcher(text);
        
        if (currencyMatcher.find()) {
            try {
                double amount = Double.parseDouble(currencyMatcher.group(1));
                result.put("amount", amount);
                result.put("currency", "USD");
                System.out.println("Extracted amount: $" + amount);
                return; // Found a good match, return
            } catch (NumberFormatException e) {
                System.err.println("Failed to parse currency amount: " + e.getMessage());
            }
        }
        
        // Look for any number that could be an amount
        Pattern amountPattern = Pattern.compile("\\b(\\d+\\.\\d{2})\\b");
        Matcher amountMatcher = amountPattern.matcher(text);
        if (amountMatcher.find()) {
            try {
                double amount = Double.parseDouble(amountMatcher.group(1));
                if (amount > 0 && amount < 10000) { // Reasonable amount range
                    result.put("amount", amount);
                    result.put("currency", "USD"); // Assume USD if no currency symbol
                    System.out.println("Extracted amount: " + amount);
                    return; // Found a reasonable amount, return
                }
            } catch (NumberFormatException e) {
                // Continue to next match
            }
        }
    }

    private void extractDate(String text, Map<String, Object> result) {
        // Multiple date patterns
        Pattern[] datePatterns = {
            Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b"), // YYYY-MM-DD
            Pattern.compile("\\b(\\d{1,2}[/-]\\d{1,2}[/-]\\d{2,4})\\b"), // MM/DD/YYYY or DD-MM-YYYY
            Pattern.compile("\\b(\\d{1,2}\\s+(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec)\\s+\\d{2,4})\\b", Pattern.CASE_INSENSITIVE) // DD Month YYYY
        };
        
        for (Pattern pattern : datePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String date = matcher.group(1);
                result.put("transactionDate", date);
                System.out.println("Extracted date: " + date);
                return; // Found a date, return
            }
        }
    }

    private void extractMerchantName(String text, Map<String, Object> result) {
        // Look for common business patterns
        Pattern[] businessPatterns = {
            Pattern.compile("\\b([A-Z][a-z]+(?:\\s+[A-Z][a-z]+){1,2})\\b"), // Two or three capitalized words
            Pattern.compile("\\b([A-Z]{2,}(?:\\s+[A-Z]{2,})+)\\b"), // Acronyms
            Pattern.compile("\\b([A-Z][a-z]+\\s+&\\s+[A-Z][a-z]+)\\b"), // Company & Company
            Pattern.compile("\\b(Walmart|Target|Starbucks|Amazon|Costco)\\b", Pattern.CASE_INSENSITIVE) // Specific common merchants
        };
        
        for (Pattern pattern : businessPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String merchant = matcher.group(1);
                // Filter out common non-business words
                if (!isCommonWord(merchant)) {
                    result.put("merchantName", merchant); // Found a merchant, return
                    System.out.println("Extracted merchant: " + merchant);
                    return;
                }
            }
        }
    }

    private void extractCategory(String text, Map<String, Object> result) {
        String lowerText = text.toLowerCase();
        
        // Category keywords
        if (lowerText.contains("grocery") || lowerText.contains("food") || lowerText.contains("restaurant") || 
            lowerText.contains("dining") || lowerText.contains("cafe") || lowerText.contains("meal")) {
            result.put("category", "Food & Dining");
        } else if (lowerText.contains("gas") || lowerText.contains("fuel") || lowerText.contains("transport") || 
                   lowerText.contains("uber") || lowerText.contains("taxi") || lowerText.contains("parking")) {
            result.put("category", "Transportation");
        } else if (lowerText.contains("shopping") || lowerText.contains("store") || lowerText.contains("mall") || 
                   lowerText.contains("clothing") || lowerText.contains("electronics")) {
            result.put("category", "Shopping");
        } else if (lowerText.contains("entertainment") || lowerText.contains("movie") || lowerText.contains("theater") || 
                   lowerText.contains("concert") || lowerText.contains("game")) {
            result.put("category", "Entertainment");
        } else if (lowerText.contains("medical") || lowerText.contains("health") || lowerText.contains("pharmacy") || 
                   lowerText.contains("doctor") || lowerText.contains("hospital")) {
            result.put("category", "Healthcare");
        } else if (lowerText.contains("utility") || lowerText.contains("fuel") || lowerText.contains("water") || 
                   lowerText.contains("internet") || lowerText.contains("phone")) {
            result.put("category", "Utilities");
        } else if (lowerText.contains("education") || lowerText.contains("school") || lowerText.contains("college") || 
                   lowerText.contains("course") || lowerText.contains("book")) {
            result.put("category", "Education");
        } else if (lowerText.contains("business") || lowerText.contains("office") || lowerText.contains("corporate")) {
            result.put("category", "Business");
        } else if (lowerText.contains("travel") || lowerText.contains("hotel") || lowerText.contains("flight")) {
            result.put("category", "Travel");
        } else {
            result.put("category", "Other");
        }
        
        System.out.println("Extracted category: " + result.get("category"));
    }

    private void extractDescription(String text, Map<String, Object> result) {
        // Look for lines that might contain descriptions
        String[] lines = text.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.length() > 10 && line.length() < 100 && 
                !line.matches(".*\\d{4}-\\d{2}-\\d{2}.*") && // Not a date
                !line.matches(".*\\$\\s*\\d+.*") && // Not an amount
                !line.matches("^[A-Z][a-z]+\\s+[A-Z][a-z]+$")) { // Not just a merchant name
                
                result.put("description", line);
                System.out.println("Extracted description: " + line);
                return;
            }
        }
        
        // If no good description found, use a generic one
        result.put("description", "Document processing");
    }

    private void extractInvoiceNumber(String text, Map<String, Object> result) {
        // Look for invoice number patterns
        Pattern[] invoicePatterns = {
            Pattern.compile("\\b(INV|INVOICE|RECEIPT)\\s*#?\\s*(\\d+)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(\\d{6,})\\b"), // 6+ digit numbers
            Pattern.compile("\\b([A-Z]{2,}\\d{4,})\\b") // Letter-number combinations
        };
        
        for (Pattern pattern : invoicePatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                String invoiceNumber = matcher.group(matcher.groupCount());
                result.put("invoiceNumber", invoiceNumber);
                System.out.println("Extracted invoice number: " + invoiceNumber);
                return;
            }
        }
    }

    private void extractTaxAmount(String text, Map<String, Object> result) {
        // Look for tax patterns
        Pattern[] taxPatterns = {
            Pattern.compile("\\b(TAX|SALES\\s+TAX)\\s*\\$?\\s*(\\d+(?:\\.\\d{2})?)\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\b(\\d+(?:\\.\\d{2})?)\\s*%\\s*TAX\\b", Pattern.CASE_INSENSITIVE)
        };
        
        for (Pattern pattern : taxPatterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    double taxAmount = Double.parseDouble(matcher.group(2));
                    result.put("taxAmount", taxAmount);
                    System.out.println("Extracted tax amount: " + taxAmount);
                    return;
                } catch (Exception e) {
                    // Continue to next pattern
                }
            }
        }
    }

    private boolean isCommonWord(String text) {
        String[] commonWords = {"the", "and", "or", "but", "in", "on", "at", "to", "for", "of", "with", "by"};
        String lowerText = text.toLowerCase();
        for (String word : commonWords) {
            if (lowerText.equals(word)) {
                return true;
            }
        }
        return false;
    }

    private void setDefaultValues(Map<String, Object> result) {
        if (!result.containsKey("merchantName")) result.put("merchantName", "");
        if (!result.containsKey("amount")) result.put("amount", 0.0);
        if (!result.containsKey("currency")) result.put("currency", "");
        if (!result.containsKey("transactionDate")) result.put("transactionDate", "");
        if (!result.containsKey("category")) result.put("category", "");
        if (!result.containsKey("description")) result.put("description", "");
        if (!result.containsKey("invoiceNumber")) result.put("invoiceNumber", "");
        if (!result.containsKey("taxAmount")) result.put("taxAmount", 0.0);
        if (!result.containsKey("totalAmount")) result.put("totalAmount", 0.0);
        if (!result.containsKey("paymentMethod")) result.put("paymentMethod", "");
        if (!result.containsKey("items")) result.put("items", new String[0]);
    }

    private Map<String, Object> createDefaultResult(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        setDefaultValues(result);
        result.put("fileName", file.getOriginalFilename());
        result.put("fileType", file.getContentType());
        result.put("fileSize", file.getSize());
        result.put("processingMethod", "Default (Processing Failed)");
        result.put("extractedText", "");
        result.put("aiConfidence", "0% (Processing Failed)");
        return result;
    }
}
