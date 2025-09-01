# Personal-Finance-Manager - Frontend

A modern React-based web application for personal finance management with AI-powered receipt scanning capabilities.

## Features

- **User Authentication**: Secure login and registration system
- **Transaction Management**: Add, edit, and delete income and expense transactions
- **Receipt Scanning**: AI-powered PDF receipt processing using Google Gemini AI
- **Financial Analytics**: Interactive charts and reports for expense tracking
- **Budget Management**: Set and track budgets by categories
- **Responsive Design**: Modern UI that works on desktop and mobile devices
- **Real-time Updates**: Live transaction updates and notifications

## Tech Stack

- **React 19.1.1** - Modern React with hooks and functional components
- **React Router 6.8.0** - Client-side routing
- **Axios 1.11.0** - HTTP client for API communication
- **Recharts 2.8.0** - Interactive charts and data visualization
- **Sass 1.91.0** - Advanced CSS preprocessing
- **Date-fns 2.30.0** - Date utility library
- **React Testing Library** - Component testing

## Prerequisites

Before running this application, make sure you have:

- **Node.js** (version 16 or higher)
- **npm** or **yarn** package manager
- **Backend API** running on `http://localhost:8080`

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd my-finance-manager-main/frontend
```

### 2. Install Dependencies

```bash
npm install
```

### 3. Environment Setup

The application is configured to proxy API requests to `http://localhost:8080` (backend server). Make sure your backend is running before starting the frontend.

### 4. Start the Development Server

// For FrontEnd

```bash
npm start
```

The application will open in your browser at `http://localhost:3000`.

## Project Structure

```
src/
├── api/                    # API service modules
│   ├── auth.js            # Authentication API calls
│   ├── axios.js           # Axios configuration
│   ├── budgets.js         # Budget management API
│   ├── categories.js      # Category management API
│   ├── expenses.js        # Expense API calls
│   ├── incomes.js         # Income API calls
│   └── transactions.js    # Transaction API calls
├── components/            # Reusable UI components
│   ├── auth/              # Authentication components
│   ├── charts/            # Data visualization components
│   ├── common/            # Shared UI components
│   ├── pagination/        # Pagination controls
│   ├── receipts/          # Receipt upload components
│   └── transactions/      # Transaction components
├── contexts/              # React Context providers
│   ├── AuthContext.js     # Authentication state management
│   └── TransactionContext.js # Transaction state management
├── hooks/                 # Custom React hooks
├── pages/                 # Main application pages
├── utils/                 # Utility functions
└── assets/                # Static assets and styles
```

## Available Scripts

- `npm start` - Runs the app in development mode
- `npm test` - Launches the test runner
- `npm run build` - Builds the app for production
- `npm run eject` - Ejects from Create React App (one-way operation)

## Key Components

### Authentication
- **LoginForm**: User login with email and password
- **RegisterForm**: New user registration
- **AuthContext**: Global authentication state management

### Transactions
- **TransactionForm**: Add/edit income and expense transactions
- **TransactionList**: Display and manage transactions
- **TransactionItem**: Individual transaction display

### Analytics
- **ExpenseByCategoryChart**: Pie chart showing expenses by category
- **ExpenseByDateChart**: Line chart showing expenses over time
- **IncomeVsExpenseChart**: Bar chart comparing income vs expenses

### Receipt Management
- **ReceiptUploader**: Upload and process PDF receipts
- **PdfHistoryUploader**: View and manage uploaded receipts

## 🔌 API Integration

The frontend communicates with the backend through RESTful APIs:

- **Authentication**: `/api/auth/*` - Login, register, token management
- **Transactions**: `/api/transactions/*` - CRUD operations for transactions
- **Receipts**: `/api/receipts/*` - Receipt upload and processing
- **Analytics**: `/api/analytics/*` - Financial reports and charts

## Key Features Explained

### AI-Powered Receipt Scanning
- Upload PDF receipts through the web interface
- Automatic extraction of transaction details using Google Gemini AI
- Support for various receipt formats and layouts
- Manual review and editing of extracted data

### Real-time Financial Tracking
- Live updates of transaction balances
- Interactive charts and visualizations
- Category-based expense analysis
- Budget tracking and alerts

### Responsive Design
- Mobile-first approach
- Cross-browser compatibility
- Modern UI with smooth animations
- Accessible design patterns

## Testing

Run the test suite:

```bash
npm test
```

The application uses React Testing Library for component testing and Jest as the test runner.

## Building for Production

Create a production build:

```bash
npm run build
```

This creates an optimized build in the `build/` folder ready for deployment.

## Security Features

- JWT-based authentication
- Secure API communication
- Input validation and sanitization
- Protected routes and components

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Support

For support and questions:
- Check the backend README for API documentation
- Review the component documentation in the code
- Open an issue in the repository

## Version History

- **v0.1.0** - Initial release with core features
  - User authentication
  - Transaction management
  - Basic analytics
  - Receipt scanning integration

 // For Backend 

 # Personal-Finance-Manager - Backend

A robust Spring Boot REST API for personal finance management with AI-powered receipt processing using Google Gemini AI.

## Features

- **RESTful API**: Complete CRUD operations for financial transactions
- **AI-Powered Receipt Processing**: Google Gemini AI integration for automatic data extraction
- **User Management**: Secure authentication and user account management
- **Financial Analytics**: Comprehensive reporting and data analysis
- **PDF Processing**: Advanced PDF parsing and text extraction
- **Database Integration**: MySQL database with JPA/Hibernate ORM
- **File Upload**: Secure file handling for receipts and documents

## Tech Stack

- **Spring Boot 3.5.5** - Modern Java framework
- **Spring Data JPA** - Database abstraction layer
- **MySQL 8.0** - Relational database
- **Apache PDFBox 2.0.29** - PDF processing library
- **Apache Tika 2.9.1** - Content detection and extraction
- **Google Gemini AI** - AI-powered receipt analysis
- **Lombok** - Java boilerplate reduction
- **Maven** - Build and dependency management

## Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+** database server
- **Google Gemini AI API Key** (for receipt processing)

## Getting Started

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

## Project Structure

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

## API Endpoints

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

## Performance Optimization

- **Database Indexing**: Optimized database queries
- **Connection Pooling**: HikariCP connection pool
- **Caching**: Spring Boot caching support
- **Async Processing**: Background task processing

