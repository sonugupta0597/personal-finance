# Finance Manager API Services

This directory contains modular API services built with Axios for the Finance Manager application.

## Structure

```
api/
├── axios.js          # Base Axios instance with interceptors
├── expenses.js       # Expense-related API calls
├── incomes.js        # Income-related API calls
├── transactions.js   # Transaction and receipt processing API calls
├── budgets.js        # Budget management API calls
├── index.js          # Central export file
└── README.md         # This file
```

## Base Configuration

The base Axios instance (`axios.js`) provides:
- Base URL: `http://localhost:8080/api`
- Request/response logging
- Error handling and logging
- 10-second timeout
- JSON content type headers

## Usage Examples

### Importing Services

```javascript
// Import specific services
import { getExpenses, addExpense } from '../api/expenses';
import { getIncomes, addIncome } from '../api/incomes';
import { uploadStatement, getTransactions } from '../api/transactions';
import { setBudget, getBudgets } from '../api/budgets';

// Or import all from index
import { 
  getExpenses, 
  addIncome, 
  uploadStatement, 
  setBudget 
} from '../api';
```

### Expenses API

```javascript
// Get paginated expenses
const expenses = await getExpenses(0, 10, '2024-01-01', '2024-12-31');

// Add new expense
const newExpense = await addExpense({
  amount: 50.00,
  category: 'groceries',
  description: 'Weekly groceries',
  date: '2024-01-15'
});

// Update expense
const updatedExpense = await updateExpense(1, {
  amount: 55.00,
  category: 'groceries',
  description: 'Weekly groceries + snacks',
  date: '2024-01-15'
});

// Delete expense
await deleteExpense(1);

// Get expense summary by category
const summary = await getExpenseSummary('2024-01-01', '2024-12-31');
```

### Incomes API

```javascript
// Get paginated incomes
const incomes = await getIncomes(0, 10, '2024-01-01', '2024-12-31');

// Add new income
const newIncome = await addIncome({
  amount: 5000.00,
  source: 'salary',
  description: 'Monthly salary',
  date: '2024-01-31'
});

// Get income summary by source
const summary = await getIncomeSummary('2024-01-01', '2024-12-31');
```

### Transactions API

```javascript
// Upload statement file
const file = document.getElementById('file-input').files[0];
const extractedTransactions = await uploadStatement(file);

// Save extracted transactions
await saveTransactions(extractedTransactions);

// Get transactions with filtering
const transactions = await getTransactions(0, 10, '2024-01-01', '2024-12-31');

// Get transaction summary
const summary = await getTransactionSummary('2024-01-01', '2024-12-31');
```

### Budgets API

```javascript
// Set budget for category
const budget = await setBudget({
  category: 'groceries',
  amount: 500.00,
  period: 'monthly'
});

// Get all budgets
const budgets = await getBudgets();

// Get budget summary
const summary = await getBudgetSummary();
```

## Error Handling

All API functions include:
- Try-catch blocks
- Console error logging
- Error re-throwing for component-level handling

```javascript
try {
  const expenses = await getExpenses();
  // Handle success
} catch (error) {
  // Handle error (already logged by API service)
  console.error('Component error handling:', error);
}
```

## Response Format

All API functions return `response.data` directly, so you get the actual data without needing to access `.data` in your components.

## File Upload

For file uploads (like `uploadStatement`), the service automatically:
- Creates FormData
- Sets correct `multipart/form-data` headers
- Handles the file upload process

## Date Formatting

Date parameters should be passed as ISO date strings (YYYY-MM-DD) for consistency with the backend API.

