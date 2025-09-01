import api from './axios';

// Get expenses with pagination and optional date filtering
export const getExpenses = async (page = 0, size = 10, startDate = null, endDate = null) => {
  try {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await api.get(`/expenses/paged?${params}`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch expenses:', error);
    throw error;
  }
};

// Get all expenses (simple list)
export const getAllExpenses = async () => {
  try {
    const response = await api.get('/expenses');
    return response.data;
  } catch (error) {
    console.error('Failed to fetch all expenses:', error);
    throw error;
  }
};

// Get expense by ID
export const getExpenseById = async (id) => {
  try {
    const response = await api.get(`/expenses/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch expense ${id}:`, error);
    throw error;
  }
};

// Add new expense
export const addExpense = async (expenseData) => {
  try {
    const response = await api.post('/expenses', expenseData);
    return response.data;
  } catch (error) {
    console.error('Failed to add expense:', error);
    throw error;
  }
};

// Update existing expense
export const updateExpense = async (id, expenseData) => {
  try {
    const response = await api.put(`/expenses/${id}`, expenseData);
    return response.data;
  } catch (error) {
    console.error(`Failed to update expense ${id}:`, error);
    throw error;
  }
};

// Delete expense
export const deleteExpense = async (id) => {
  try {
    await api.delete(`/expenses/${id}`);
    return true;
  } catch (error) {
    console.error(`Failed to delete expense ${id}:`, error);
    throw error;
  }
};

// Get expense summary by category
export const getExpenseSummary = async (startDate = null, endDate = null) => {
  try {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await api.get(`/expenses/summary?${params}`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch expense summary:', error);
    throw error;
  }
};
