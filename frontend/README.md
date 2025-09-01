# My Finance Manager

A comprehensive web application for managing personal finances, tracking income and expenses, and analyzing financial data through interactive charts and reports.

## Features

### 🏦 Transaction Management
- **Create Income/Expense Entries**: Add new financial transactions with detailed information
- **Categorize Transactions**: Organize transactions by predefined categories (Food & Dining, Transportation, Housing, etc.)
- **Edit & Delete**: Modify existing transactions or remove them as needed
- **Filter & Search**: Find transactions by date range, type, and category

### 📊 Financial Analytics
- **Dashboard Overview**: Get a quick summary of your financial status
- **Interactive Charts**: 
  - Expenses by Category (Pie Chart)
  - Income vs Expenses over Time (Bar Chart)
  - Expenses Over Time (Line Chart)
- **Real-time Data**: Charts automatically update as you add/modify transactions

### 📈 Comprehensive Reports
- **Date Range Selection**: Choose from preset ranges (1 month, 3 months, 6 months, 1 year) or custom dates
- **Financial Summary**: View total income, expenses, net amount, and transaction count
- **Export Functionality**: Download financial reports in JSON format
- **Filtered Analysis**: Analyze specific time periods for better insights

### 🧾 Receipt Processing
- **Image Receipts**: Upload JPEG, PNG, and WebP images of receipts
- **PDF Receipts**: Process PDF receipts for expense extraction
- **Automatic Data Extraction**: Extract key information like:
  - Total amount
  - Merchant name
  - Date
  - Category
  - Individual items
  - Tax amounts
- **Transaction Creation**: Automatically create transactions from extracted receipt data

### 🔐 User Authentication
- **Secure Login/Registration**: User account management with email and password
- **Token-based Authentication**: JWT tokens for secure API access
- **Session Management**: Persistent login sessions

## Technology Stack

- **Frontend**: React 19 with modern hooks and context API
- **Styling**: SCSS with responsive design and modern UI components
- **Charts**: Recharts library for interactive data visualization
- **Routing**: React Router for navigation between pages
- **State Management**: React Context API for global state
- **Date Handling**: date-fns for date manipulation and formatting
- **Build Tool**: Create React App with optimized production builds

## Getting Started

### Prerequisites
- Node.js (version 16 or higher)
- npm or yarn package manager

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd my_finance_manager/frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start the development server**
   ```bash
   npm start
   ```

4. **Open your browser**
   Navigate to `http://localhost:3000`

### Building for Production

```bash
npm run build
```

The build artifacts will be stored in the `build/` directory.

## Project Structure

```
src/
├── api/                    # API service functions
│   ├── auth.js            # Authentication API calls
│   ├── transactions.js    # Transaction CRUD operations
│   └── ...
├── components/            # Reusable UI components
│   ├── auth/             # Authentication components
│   ├── charts/           # Chart components
│   ├── common/           # Common UI elements
│   ├── receipts/         # Receipt processing components
│   └── transactions/     # Transaction management components
├── contexts/             # React Context providers
│   ├── AuthContext.js    # Authentication state
│   └── TransactionContext.js # Transaction state
├── hooks/                # Custom React hooks
├── pages/                # Main application pages
├── utils/                # Utility functions
└── assets/               # Static assets and styles
```

## Usage Guide

### 1. Getting Started
- Register a new account or log in with existing credentials
- Start by adding your first transaction

### 2. Adding Transactions
- Navigate to the Transactions page
- Click "Add Transaction"
- Fill in the required fields:
  - Type (Income/Expense)
  - Amount
  - Description
  - Category
  - Date (optional)
  - Notes (optional)

### 3. Managing Transactions
- View all transactions in a list format
- Use filters to find specific transactions
- Edit or delete transactions as needed
- View transaction details and history

### 4. Analyzing Finances
- Check the Dashboard for an overview
- Use the Reports page for detailed analysis
- Select different date ranges for insights
- Export reports for external analysis

### 5. Processing Receipts
- Go to the Receipts page
- Choose between Image or PDF upload
- Select your receipt files
- Review extracted information
- Create transactions automatically

## API Integration

The application is designed to work with a backend API. The current implementation includes:

- **Base URL**: Configurable API endpoint
- **Authentication**: Bearer token-based API calls
- **Endpoints**: 
  - `/auth/login` - User authentication
  - `/auth/register` - User registration
  - `/transactions` - CRUD operations for transactions
  - `/receipts/upload` - Receipt processing

## Customization

### Adding New Categories
Modify the `CATEGORIES` object in `TransactionForm.js` to add new income or expense categories.

### Styling
The application uses SCSS for styling. Main styles are in `src/assets/styles/index.scss` with:
- CSS variables for consistent theming
- Responsive design breakpoints
- Component-specific styles
- Modern UI patterns

### Charts
Charts are built with Recharts and can be customized by:
- Modifying chart types and configurations
- Adding new chart components
- Customizing colors and styling
- Implementing new data visualizations

## Browser Support

- Chrome (latest)
- Firefox (latest)
- Safari (latest)
- Edge (latest)

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions, please open an issue in the repository or contact the development team.

---

**Note**: This is a frontend application. For full functionality, you'll need to implement or connect to a backend API that provides the required endpoints for authentication, transaction management, and receipt processing.
