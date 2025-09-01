// Export all API services
export { default as api } from './axios';

// Auth API
export {
  registerApi,
  loginApi,
  meApi,
  logoutApi,
} from './auth';

// Expenses API
export {
  getExpenses,
  getAllExpenses,
  getExpenseById,
  addExpense,
  updateExpense,
  deleteExpense,
  getExpenseSummary,
} from './expenses';

// Incomes API
export {
  getIncomes,
  getAllIncomes,
  getIncomeById,
  addIncome,
  updateIncome,
  deleteIncome,
  getIncomeSummary,
} from './incomes';

// Transactions API
export {
  uploadStatement,
  saveTransactions,
  getTransactions,
  getTransactionSummary,
  getTransactionById,
  updateTransaction,
  deleteTransaction,
} from './transactions';

// Budgets API
export {
  setBudget,
  getBudgets,
  getBudgetById,
  updateBudget,
  deleteBudget,
  getBudgetSummary,
  getBudgetByCategory,
} from './budgets';
