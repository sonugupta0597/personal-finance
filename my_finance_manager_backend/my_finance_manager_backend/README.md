# My Finance Manager Backend

A Spring Boot backend application for managing personal finances with AI-powered receipt and transaction processing.

## Features

- **User Management**: User registration, authentication, and profile management
- **Expense Tracking**: Record and categorize expenses
- **Income Management**: Track income sources and amounts
- **Receipt Processing**: Upload and process receipts using Gemini AI
- **Transaction History**: View and analyze financial transactions
- **AI-Powered Data Extraction**: Extract transaction information from PDFs and images using Google's Gemini AI
- **Financial Reports**: Generate summaries and reports

## AI Integration

### Gemini AI for Document Processing

The backend integrates with Google's Gemini AI to intelligently extract financial information from:

- **PDF Bank Statements**: Automatically extract transactions, dates, amounts, and descriptions
- **Receipt Images**: Identify merchants, amounts, categories, and transaction details
- **Financial Documents**: Parse complex financial statements and reports

#### How It Works

1. **Document Upload**: Users upload PDFs or images through the frontend
2. **AI Processing**: Gemini AI analyzes the document content and extracts structured data
3. **Data Validation**: The system validates and processes the extracted information
4. **Database Storage**: Clean, structured data is stored in the database
5. **Fallback Processing**: If AI extraction fails, the system falls back to pattern-based text extraction

#### Supported Document Types

- **PDFs**: Bank statements, credit card statements, financial reports
- **Images**: Receipt photos, invoice images, document scans
- **Text Files**: Plain text financial documents

#### AI Extraction Capabilities

- **Transaction Details**: Date, description, amount, type (income/expense)
- **Receipt Information**: Merchant name, amount, currency, date, category
- **Smart Categorization**: Automatic classification of expenses and income
- **Data Validation**: Ensures extracted data meets business rules

## Technology Stack

- **Framework**: Spring Boot 3.5.5
- **Database**: MySQL 8.0 with JPA/Hibernate
- **AI Service**: Google Gemini AI (gemini-pro and gemini-pro-vision models)
- **File Processing**: Apache Tika for text extraction
- **Build Tool**: Maven
- **Java Version**: 11

## Setup Instructions

### Prerequisites

- Java 11 or higher
- Maven 3.6+
- MySQL 8.0+
- Gemini AI API key

### Configuration

1. **Database Setup**
   ```sql
   CREATE DATABASE my_finance_manager;
   ```

2. **Application Properties**
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/my_finance_manager
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # Gemini AI Configuration
   gemini.api.key=your_gemini_api_key
   gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-pro-vision:generateContent
   gemini.model.name=gemini-pro-vision
   
   # File Upload Configuration
   spring.servlet.multipart.max-file-size=10MB
   spring.servlet.multipart.max-request-size=10MB
   ```

3. **Gemini AI Setup**
   - Get your API key from [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Update the `gemini.api.key` property in `application.properties`

### Running the Application

1. **Clone and Navigate**
   ```bash
   cd my_finance_manager_backend
   ```

2. **Build the Project**
   ```bash
   ./mvnw clean install
   ```

3. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Run Tests**
   ```bash
   ./mvnw test
   ```

## API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login

### Expenses
- `GET /api/expenses` - List all expenses
- `POST /api/expenses` - Create new expense
- `GET /api/expenses/{id}` - Get expense by ID
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense

### Income
- `GET /api/income` - List all income
- `POST /api/income` - Create new income
- `GET /api/income/{id}` - Get income by ID
- `PUT /api/income/{id}` - Update income
- `DELETE /api/income/{id}` - Delete income

### Receipts
- `POST /api/receipts/upload` - Upload receipt for AI processing
- `GET /api/receipts` - List all receipts
- `GET /api/receipts/{id}` - Get receipt by ID
- `DELETE /api/receipts/{id}` - Delete receipt
- `POST /api/receipts/{id}/reprocess` - Reprocess receipt with AI

### Transactions
- `POST /api/transactions/extract` - Extract transactions from PDF
- `GET /api/transactions` - List transactions with pagination
- `GET /api/transactions/summary` - Get financial summary

## AI Processing Workflow

### PDF Transaction Extraction

1. **Upload PDF**: User uploads a bank statement or financial document
2. **Text Extraction**: Apache Tika extracts raw text content
3. **AI Analysis**: Gemini AI analyzes the text and identifies transactions
4. **Data Parsing**: System parses AI response into structured transaction data
5. **Validation**: Data is validated against business rules
6. **Storage**: Valid transactions are saved to the database

### Receipt Image Processing

1. **Image Upload**: User uploads a receipt photo
2. **Vision Analysis**: Gemini Vision AI analyzes the image content
3. **Data Extraction**: AI extracts merchant, amount, date, and category
4. **Smart Categorization**: System automatically categorizes the expense
5. **Storage**: Receipt data is saved with AI confidence scores

## Error Handling

The system includes comprehensive error handling:

- **AI Service Failures**: Graceful fallback to pattern-based extraction
- **File Processing Errors**: Detailed error messages and logging
- **Data Validation**: Input validation and business rule enforcement
- **API Errors**: Standardized error responses with appropriate HTTP status codes

## Testing

The application includes comprehensive test coverage:

- **Unit Tests**: Service layer testing with Mockito
- **Integration Tests**: End-to-end API testing
- **AI Service Tests**: Mocked Gemini AI responses for testing

Run tests with:
```bash
./mvnw test
```

## Performance Considerations

- **File Size Limits**: 10MB maximum file size for uploads
- **AI API Rate Limiting**: Respects Gemini AI rate limits
- **Database Optimization**: Efficient queries and indexing
- **Caching**: Spring Boot's built-in caching mechanisms

## Security Features

- **JWT Authentication**: Secure token-based authentication
- **Input Validation**: Comprehensive input sanitization
- **File Upload Security**: File type and size validation
- **API Security**: Protected endpoints with authentication

## Monitoring and Logging

- **Application Logs**: Detailed logging for debugging
- **AI Processing Logs**: Track AI extraction success/failure rates
- **Performance Metrics**: Monitor API response times
- **Error Tracking**: Comprehensive error logging and reporting

## Future Enhancements

- **Batch Processing**: Process multiple documents simultaneously
- **Advanced Analytics**: Machine learning for spending patterns
- **Multi-language Support**: Process documents in multiple languages
- **Real-time Processing**: WebSocket-based real-time updates
- **Mobile Optimization**: Enhanced mobile API endpoints

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions:
- Create an issue in the repository
- Check the documentation
- Review the test cases for usage examples
