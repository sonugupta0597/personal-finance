# Gemini AI Integration Demo

This document demonstrates how the Gemini AI integration works in the Personal-Finance-Manager backend for extracting transaction information from PDFs and images.

## Overview

The backend now includes a fully functional Gemini AI integration that can:
- Extract transactions from PDF bank statements
- Process receipt images to identify financial details
- Provide intelligent categorization of expenses and income
- Fall back to pattern-based extraction if AI fails

## How It Works

### 1. PDF Transaction Extraction

When a user uploads a PDF (e.g., bank statement), the system:

1. **Extracts Text**: Uses Apache Tika to extract raw text from the PDF
2. **AI Analysis**: Sends the text to Gemini AI with a specialized prompt
3. **Response Parsing**: Parses the AI response to extract structured transaction data
4. **Data Validation**: Validates the extracted data against business rules
5. **Database Storage**: Saves valid transactions to the database

#### Example Prompt for Gemini AI:
```
Analyze this financial statement or bank statement PDF and extract all transactions. 
For each transaction, identify: date, description, amount, and type (income/expense). 
Return the data in this exact JSON format: 
[{"date": "YYYY-MM-DD", "description": "transaction description", "amount": number, "type": "INCOME" or "EXPENSE"}] 
Only return valid JSON array with these exact field names. 
If you can't determine the type, use "EXPENSE" for negative amounts and "INCOME" for positive amounts.
```

#### Expected AI Response:
```json
[
  {
    "date": "2024-01-15",
    "description": "Grocery Store Purchase",
    "amount": 45.67,
    "type": "EXPENSE"
  },
  {
    "date": "2024-01-16",
    "description": "Salary Deposit",
    "amount": 2500.00,
    "type": "INCOME"
  }
]
```

### 2. Receipt Image Processing

For receipt images, the system:

1. **Vision Analysis**: Uses Gemini Vision AI to analyze the image content
2. **Data Extraction**: Identifies merchant, amount, date, and category
3. **Smart Categorization**: Automatically classifies the expense
4. **Confidence Scoring**: Provides confidence levels for extracted data

#### Example Receipt Processing:
```
Input: Receipt image from a restaurant
Output: {
  "merchantName": "Joe's Diner",
  "amount": 28.50,
  "currency": "USD",
  "transactionDate": "2024-01-15",
  "category": "Food & Dining",
  "description": "Lunch meal",
  "aiConfidence": "95%"
}
```

## API Endpoints

### Extract Transactions from PDF
```http
POST /api/transactions/extract
Content-Type: multipart/form-data

file: [PDF file]
```

**Response:**
```json
[
  {
    "date": "2024-01-15",
    "description": "Grocery Store Purchase",
    "amount": 45.67,
    "type": "EXPENSE"
  }
]
```

### Upload Receipt for AI Processing
```http
POST /api/receipts/upload
Content-Type: multipart/form-data

file: [Image file]
```

**Response:**
```json
{
  "id": 1,
  "fileName": "receipt.jpg",
  "merchantName": "Joe's Diner",
  "amount": 28.50,
  "currency": "USD",
  "transactionDate": "2024-01-15",
  "category": "Food & Dining",
  "description": "Lunch meal",
  "aiConfidence": "95%",
  "processed": true,
  "uploadDate": "2024-01-15T12:00:00Z"
}
```

## Fallback Processing

If Gemini AI fails to extract information, the system automatically falls back to pattern-based text extraction using regex patterns:

1. **Date Patterns**: Recognizes multiple date formats (YYYY-MM-DD, MM/DD/YYYY, etc.)
2. **Amount Patterns**: Identifies currency amounts with proper decimal handling
3. **Description Extraction**: Captures transaction descriptions from surrounding text
4. **Type Inference**: Determines income/expense based on amount sign

## Error Handling

The system includes comprehensive error handling:

- **AI Service Failures**: Graceful fallback to pattern-based extraction
- **Invalid Responses**: JSON parsing errors are caught and logged
- **File Processing Errors**: Detailed error messages for debugging
- **Data Validation**: Business rule enforcement with clear error messages

## Testing the Integration

### 1. Run the Tests
```bash
./mvnw test
```

This will run all tests including:
- `TransactionHistoryServiceTest` - Tests transaction extraction
- `GeminiAIServiceTest` - Tests AI service functionality

### 2. Manual Testing
1. Start the application: `./mvnw spring-boot:run`
2. Upload a PDF bank statement to `/api/transactions/extract`
3. Upload a receipt image to `/api/receipts/upload`
4. Check the extracted data in the response

## Configuration

### Required Properties
```properties
# Gemini AI Configuration
gemini.api.key=your_api_key_here
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent
gemini.model.name=gemini-pro-vision

# File Upload Limits
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

### API Key Setup
1. Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
2. Create a new API key
3. Update the `gemini.api.key` property

## Performance Considerations

- **File Size**: Maximum 10MB per file
- **API Rate Limits**: Respects Gemini AI rate limits
- **Processing Time**: AI processing adds 2-5 seconds per file
- **Caching**: Consider implementing response caching for repeated documents

## Troubleshooting

### Common Issues

1. **"No transactions extracted from PDFs"**
   - Check Gemini API key configuration
   - Verify API quota and limits
   - Review server logs for AI service errors

2. **AI Processing Failures**
   - Ensure file format is supported
   - Check file size limits
   - Verify network connectivity to Gemini API

3. **JSON Parsing Errors**
   - Review AI response format
   - Check prompt engineering
   - Verify response validation logic

### Debug Mode
Enable debug logging in `application.properties`:
```properties
logging.level.com.my_finance_manager_backend.service=DEBUG
logging.level.com.my_finance_manager_backend.service.GeminiAIService=DEBUG
```

## Future Enhancements

- **Batch Processing**: Process multiple documents simultaneously
- **Advanced Prompting**: Dynamic prompts based on document type
- **Response Validation**: AI-powered response quality assessment
- **Multi-language Support**: Process documents in multiple languages
- **Real-time Processing**: WebSocket-based progress updates

## Conclusion

The Gemini AI integration provides a robust, intelligent solution for extracting financial information from various document types. With proper fallback mechanisms and comprehensive error handling, it ensures reliable data extraction while maintaining system stability.

The integration is production-ready and includes comprehensive testing, making it suitable for real-world financial management applications.
