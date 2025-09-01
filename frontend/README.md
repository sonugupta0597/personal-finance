# Personal-Finance-Manager - Frontend

A modern React-based web application for personal finance management with AI-powered receipt scanning capabilities.

## 🚀 Features

- **User Authentication**: Secure login and registration system
- **Transaction Management**: Add, edit, and delete income and expense transactions
- **Receipt Scanning**: AI-powered PDF receipt processing using Google Gemini AI
- **Financial Analytics**: Interactive charts and reports for expense tracking
- **Budget Management**: Set and track budgets by categories
- **Responsive Design**: Modern UI that works on desktop and mobile devices
- **Real-time Updates**: Live transaction updates and notifications

## 🛠️ Tech Stack

- **React 19.1.1** - Modern React with hooks and functional components
- **React Router 6.8.0** - Client-side routing
- **Axios 1.11.0** - HTTP client for API communication
- **Recharts 2.8.0** - Interactive charts and data visualization
- **Sass 1.91.0** - Advanced CSS preprocessing
- **Date-fns 2.30.0** - Date utility library
- **React Testing Library** - Component testing

## 📋 Prerequisites

Before running this application, make sure you have:

- **Node.js** (version 16 or higher)
- **npm** or **yarn** package manager
- **Backend API** running on `http://localhost:8080`

## 🚀 Getting Started

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

```bash
npm start
```

The application will open in your browser at `http://localhost:3000`.

## 📁 Project Structure

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

## 🔧 Available Scripts

- `npm start` - Runs the app in development mode
- `npm test` - Launches the test runner
- `npm run build` - Builds the app for production
- `npm run eject` - Ejects from Create React App (one-way operation)

## 🎨 Key Components

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

## 🎯 Key Features Explained

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

## 🧪 Testing

Run the test suite:

```bash
npm test
```

The application uses React Testing Library for component testing and Jest as the test runner.

## 📦 Building for Production

Create a production build:

```bash
npm run build
```

This creates an optimized build in the `build/` folder ready for deployment.

## 🔒 Security Features

- JWT-based authentication
- Secure API communication
- Input validation and sanitization
- Protected routes and components

## 🌐 Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

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
- Check the backend README for API documentation
- Review the component documentation in the code
- Open an issue in the repository

## 🔄 Version History

- **v0.1.0** - Initial release with core features
  - User authentication
  - Transaction management
  - Basic analytics
  - Receipt scanning integration
