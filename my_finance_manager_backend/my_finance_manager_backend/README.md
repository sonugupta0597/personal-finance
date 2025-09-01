# Personal-Finance-Manager - Backend

A robust Spring Boot REST API for personal finance management with AI-powered receipt processing using Google Gemini AI.

## 🚀 Features

- **RESTful API**: Complete CRUD operations for financial transactions
- **AI-Powered Receipt Processing**: Google Gemini AI integration for automatic data extraction
- **User Management**: Secure authentication and user account management
- **Financial Analytics**: Comprehensive reporting and data analysis
- **PDF Processing**: Advanced PDF parsing and text extraction
- **Database Integration**: MySQL database with JPA/Hibernate ORM
- **File Upload**: Secure file handling for receipts and documents

## 🛠️ Tech Stack

- **Spring Boot 3.5.5** - Modern Java framework
- **Spring Data JPA** - Database abstraction layer
- **MySQL 8.0** - Relational database
- **Apache PDFBox 2.0.29** - PDF processing library
- **Apache Tika 2.9.1** - Content detection and extraction
- **Google Gemini AI** - AI-powered receipt analysis
- **Lombok** - Java boilerplate reduction
- **Maven** - Build and dependency management

## 📋 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+** database server
- **Google Gemini AI API Key** (for receipt processing)

## 🚀 Getting Started

### 1. Database Setup

1. **Install MySQL** if not already installed
2. **Create Database**:
   ```sql
   CREATE DATABASE finance_db;
   ```
3. **Configure Database Connection** in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

### 2. API Configuration

1. **Get Google Gemini AI API Key**:
   - Visit [Google AI Studio](https://makersuite.google.com/app/apikey)
   - Create a new API key
   - Add it to `application.properties`:
   ```properties
   gemini.api.key=your_gemini_api_key
   ```

### 3. Build and Run

```bash
# Navigate to the backend directory
cd my_finance_manager_backend/my_finance_manager_backend

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

## 📁 Project Structure

```
src/main/java/com/my_finance_manager_backend/
├── config/                     # Configuration classes
│   ├── GeminiConfig.java      # Gemini AI configuration
│   ├── SecurityConfig.java    # Security settings
│   └── WebConfig.java         # Web configuration
├── controller/                 # REST API controllers
│   ├── BillScanController.java # Receipt processing endpoints
│   ├── ExpenseController.java  # Expense management
│   ├── IncomeController.java   # Income management
│   ├── ReceiptController.java  # Receipt upload/management
│   ├── TransactionHistoryController.java # Transaction history
│   └── UserController.java     # User management
├── dto/                        # Data Transfer Objects
│   ├── BillScanResultDTO.java  # AI scan results
│   ├── ExpenseRequest.java     # Expense request/response
│   ├── IncomeRequest.java      # Income request/response
│   ├── ReceiptDTO.java         # Receipt data
│   └── TransactionDTO.java     # Transaction data
├── exception/                  # Exception handling
│   ├── GlobalExceptionHandler.java # Global error handling
│   └── ResourceNotFoundException.java # Custom exceptions
├── model/                      # Entity models
│   ├── Expense.java           # Expense entity
│   ├── Income.java            # Income entity
│   ├── Receipt.java           # Receipt entity
│   └── User.java              # User entity
├── repository/                 # Data access layer
│   ├── ExpenseRepository.java  # Expense data operations
│   ├── IncomeRepository.java   # Income data operations
│   ├── ReceiptRepository.java  # Receipt data operations
│   └── UserRepository.java     # User data operations
├── security/                   # Security components
│   ├── CustomUserDetailsService.java # User details service
│   ├── JwtAuthenticationFilter.java # JWT filter
│   └── JwtUtil.java           # JWT utilities
├── service/                    # Business logic layer
│   ├── ExpenseService.java     # Expense business logic
│   ├── GeminiAIService.java    # AI integration service
│   ├── IncomeService.java      # Income business logic
│   ├── ReceiptService.java     # Receipt processing service
│   ├── TransactionHistoryService.java # Transaction history
│   └── UserService.java        # User business logic
└── MyFinanceManagerBackendApplication.java # Main application class
```

## 🔌 API Endpoints

### Authentication
- `POST /api/auth/register` - User registration
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout

### Transactions
- `GET /api/transactions` - Get all transactions
- `POST /api/transactions` - Create new transaction
- `GET /api/transactions/{id}` - Get transaction by ID
- `PUT /api/transactions/{id}` - Update transaction
- `DELETE /api/transactions/{id}` - Delete transaction

### Expenses
- `GET /api/expenses` - Get all expenses
- `POST /api/expenses` - Create new expense
- `GET /api/expenses/{id}` - Get expense by ID
- `PUT /api/expenses/{id}` - Update expense
- `DELETE /api/expenses/{id}` - Delete expense

### Income
- `GET /api/incomes` - Get all income entries
- `POST /api/incomes` - Create new income entry
- `GET /api/incomes/{id}` - Get income by ID
- `PUT /api/incomes/{id}` - Update income
- `DELETE /api/incomes/{id}` - Delete income

### Receipt Processing
- `POST /api/receipts/upload` - Upload and process receipt
- `GET /api/receipts` - Get all receipts
- `GET /api/receipts/{id}` - Get receipt by ID
- `DELETE /api/receipts/{id}` - Delete receipt

### Analytics
- `GET /api/analytics/summary` - Get financial summary
- `GET /api/analytics/expenses-by-category` - Expenses by category
- `GET /api/analytics/income-vs-expense` - Income vs expense comparison

## 🤖 AI Integration

### Google Gemini AI Features
- **Receipt Text Extraction**: Extract text from PDF receipts
- **Data Parsing**: Parse amounts, dates, merchants, and items
- **Category Detection**: Automatically categorize transactions
- **Error Handling**: Robust error handling for AI processing

### Configuration
```properties
# Gemini AI Configuration
gemini.api.key=your_api_key_here
gemini.api.url=https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent
gemini.model.name=gemini-1.5-flash
```

## 📊 Database Schema

### Users Table
- `id` (Primary Key)
- `email` (Unique)
- `password` (Encrypted)
- `created_at`
- `updated_at`

### Expenses Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `amount`
- `description`
- `category`
- `date`
- `notes`
- `created_at`
- `updated_at`

### Income Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `amount`
- `description`
- `category`
- `date`
- `notes`
- `created_at`
- `updated_at`

### Receipts Table
- `id` (Primary Key)
- `user_id` (Foreign Key)
- `filename`
- `file_path`
- `file_size`
- `upload_date`
- `processed_data` (JSON)
- `status`

## 🔧 Configuration

### Application Properties
```properties
# Server Configuration
server.port=8080
spring.application.name=my_finance_manager_backend

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/finance_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Gemini AI Configuration
gemini.api.key=your_gemini_api_key
gemini.api.url=https://generativelanguage.googleapis.com/v1/models/gemini-1.5-flash:generateContent
gemini.model.name=gemini-1.5-flash
```

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

### Test Coverage
- **Unit Tests**: Service layer testing
- **Integration Tests**: API endpoint testing
- **AI Service Tests**: Gemini AI integration testing

## 📦 Building for Production

### Create JAR File
```bash
mvn clean package
```

### Run Production Build
```bash
java -jar target/my_finance_manager_backend-0.0.1-SNAPSHOT.jar
```

## 🔒 Security Features

- **JWT Authentication**: Token-based authentication
- **Password Encryption**: Secure password hashing
- **Input Validation**: Request validation and sanitization
- **CORS Configuration**: Cross-origin resource sharing setup
- **File Upload Security**: Secure file handling

## 🚀 Deployment

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/my_finance_manager_backend-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Environment Variables
```bash
export SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host:3306/finance_db
export SPRING_DATASOURCE_USERNAME=your_username
export SPRING_DATASOURCE_PASSWORD=your_password
export GEMINI_API_KEY=your_gemini_api_key
```

## 📈 Performance Optimization

- **Database Indexing**: Optimized database queries
- **Connection Pooling**: HikariCP connection pool
- **Caching**: Spring Boot caching support
- **Async Processing**: Background task processing

## 🔍 Monitoring and Logging

- **Spring Boot Actuator**: Health checks and metrics
- **Logging**: Structured logging with SLF4J
- **Error Tracking**: Global exception handling
- **Performance Monitoring**: Request/response timing

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🆘 Support

For support and questions:
- Check the API documentation
- Review the test cases for usage examples
- Open an issue in the repository

## 🔄 Version History

- **v0.0.1-SNAPSHOT** - Initial release
  - Core CRUD operations
  - AI-powered receipt processing
  - User authentication
  - Financial analytics
  - PDF processing capabilities

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Google Gemini AI Documentation](https://ai.google.dev/docs)
- [MySQL Documentation](https://dev.mysql.com/doc/)
- [Apache PDFBox Documentation](https://pdfbox.apache.org/)
