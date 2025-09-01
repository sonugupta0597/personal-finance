# PDF Processing Troubleshooting Guide

## 🚨 **Issue: Empty AI Extraction Results**

If you're getting empty results like this:
```json
{
    "id": 3,
    "fileName": "bill.pdf",
    "fileType": "application/pdf",
    "fileSize": 67610,
    "uploadedAt": "2025-09-01T00:35:11.32187",
    "processed": true,
    "extractedText": "",
    "merchantName": "",
    "amount": 0.0,
    "currency": "",
    "transactionDate": "",
    "category": "",
    "description": "",
    "aiConfidence": "0%"
}
```

## 🔍 **Step-by-Step Diagnosis**

### **1. Check Application Logs**

Start your application and look for these debug messages:
```bash
./mvnw spring-boot:run
```

You should see detailed logging like:
```
=== Starting AI extraction for file: bill.pdf ===
File type: application/pdf
File size: 67610 bytes
Extracted text length: 1234 characters
First 200 characters: [PDF content preview]
AI Prompt: [The prompt being sent to AI]
Calling Gemini AI API...
AI Response received, length: 567 characters
AI Response (first 300 chars): [AI response preview]
Parsed result: {merchantName=..., amount=..., ...}
=== AI extraction completed ===
```

### **2. Common Issues and Solutions**

#### **Issue A: No Text Extracted from PDF**
**Symptoms**: `Extracted text length: 0 characters`
**Causes**: 
- PDF is image-based (scanned document)
- PDF is corrupted or password-protected
- PDF contains no text content

**Solutions**:
1. **Check PDF Content**: Open the PDF in a text editor to see if it contains text
2. **Use Image Processing**: If it's a scanned document, convert to image and use receipt upload
3. **Check PDF Quality**: Ensure the PDF is not corrupted

#### **Issue B: AI API Call Fails**
**Symptoms**: `AI Response received, length: 0 characters` or error messages
**Causes**:
- Invalid API key
- Network connectivity issues
- API rate limits exceeded
- Wrong API endpoint

**Solutions**:
1. **Verify API Key**: Check `application.properties` for correct Gemini API key
2. **Check Network**: Ensure your server can reach Google's APIs
3. **Check API Quota**: Verify your Gemini API quota is not exceeded

#### **Issue C: AI Response Parsing Fails**
**Symptoms**: `Parsed result: {}` (empty result)
**Causes**:
- AI response is not in expected format
- JSON parsing errors
- Response structure changed

**Solutions**:
1. **Check AI Response**: Look at the logged AI response
2. **Verify JSON Format**: Ensure response contains valid JSON
3. **Check Prompt**: Verify the prompt is generating proper responses

## 🛠️ **Testing Steps**

### **Step 1: Test with Simple PDF**
Create a simple test PDF with clear text:
```
Invoice
Date: 2024-01-15
Merchant: Test Store
Amount: $25.50
Category: Shopping
Description: Test purchase
```

### **Step 2: Test API Endpoints**
```bash
# Test receipt upload (should work for images)
curl -X POST \
  -F "file=@receipt.jpg" \
  http://localhost:8080/api/receipts/upload

# Test PDF transaction extraction
curl -X POST \
  -F "file=@test.pdf" \
  http://localhost:8080/api/transactions/extract
```

### **Step 3: Check Database**
Verify the receipt was saved:
```sql
SELECT * FROM receipt ORDER BY id DESC LIMIT 1;
```

## 🔧 **Configuration Checks**

### **1. Verify application.properties**
```properties
# Gemini AI Configuration
gemini.api.key=AIzaSyDtYKcz2Efp2mTuhoB4oW18-uNcUD-7sxY
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent
gemini.model.name=gemini-pro-vision

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### **2. Check API Key Validity**
Test your API key directly:
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -H "x-goog-api-key: YOUR_API_KEY" \
  -d '{"contents":[{"parts":[{"text":"Hello, how are you?"}]}]}' \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
```

## 📊 **Expected Behavior**

### **Successful PDF Processing**
```
=== Starting AI extraction for file: bill.pdf ===
File type: application/pdf
File size: 67610 bytes
Extracted text length: 1234 characters
First 200 characters: Invoice from ABC Company...
AI Prompt: Analyze this document (PDF) and extract financial information...
Calling Gemini AI API...
AI Response received, length: 567 characters
AI Response (first 300 chars): {"merchantName": "ABC Company", "amount": 150.00...
Parsed result: {merchantName=ABC Company, amount=150.0, currency=USD, ...}
=== AI extraction completed ===
```

### **Successful Receipt Processing**
```
=== Starting AI extraction for file: receipt.jpg ===
File type: image/jpeg
File size: 45678 bytes
Extracted text length: 0 characters
AI Prompt: Analyze this receipt image and extract...
Calling Gemini AI API...
AI Response received, length: 234 characters
AI Response (first 300 chars): {"merchantName": "Grocery Store", "amount": 45.67...
Parsed result: {merchantName=Grocery Store, amount=45.67, currency=USD, ...}
=== AI extraction completed ===
```

## 🚨 **Emergency Fallback**

If AI processing completely fails, the system will:
1. **Save Basic Receipt Info**: File metadata without AI extraction
2. **Use Pattern Matching**: Basic regex-based extraction
3. **Provide Error Details**: Clear error messages for debugging

## 📞 **Getting Help**

### **1. Enable Debug Logging**
Add to `application.properties`:
```properties
logging.level.com.my_finance_manager_backend.service=DEBUG
logging.level.com.my_finance_manager_backend.service.GeminiAIService=DEBUG
```

### **2. Check Console Output**
Look for:
- File processing steps
- AI API calls
- Response parsing
- Error messages

### **3. Common Error Messages**
- `"Failed to process file: ..."` - Check file format and content
- `"Gemini API call failed with status: ..."` - Check API key and network
- `"AI processing failed: ..."` - Check AI service configuration

## ✅ **Quick Fix Checklist**

- [ ] PDF contains extractable text (not just images)
- [ ] API key is valid and has quota
- [ ] Network can reach Google APIs
- [ ] File size is under 10MB
- [ ] File format is supported (PDF, JPEG, PNG)
- [ ] Debug logging is enabled
- [ ] Application is running on correct port

## 🔮 **Advanced Troubleshooting**

### **Test PDF Text Extraction**
```java
// Add this to your service temporarily
String extractedText = tika.parseToString(file.getInputStream());
System.out.println("Raw extracted text: " + extractedText);
```

### **Test AI API Directly**
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -H "x-goog-api-key: YOUR_KEY" \
  -d '{"contents":[{"parts":[{"text":"Extract from this: Invoice ABC Company $150.00"}]}]}' \
  "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent"
```

### **Check File Content**
```bash
# For PDFs
file your_file.pdf
# For images
file your_image.jpg
```

---

**Remember**: The system now has comprehensive debugging and fallback mechanisms. If AI fails, pattern-based extraction will attempt to extract basic information from the document.
