import api from './axios';

// Set budget for categories
export const setBudget = async (budgetData) => {
  try {
    const response = await api.post('/budgets', budgetData);
    return response.data;
  } catch (error) {
    console.error('Failed to set budget:', error);
    throw error;
  }
};

// Get all budgets
export const getBudgets = async () => {
  try {
    const response = await api.get('/budgets');
    return response.data;
  } catch (error) {
    console.error('Failed to fetch budgets:', error);
    throw error;
  }
};

// Get budget by ID
export const getBudgetById = async (id) => {
  try {
    const response = await api.get(`/budgets/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch budget ${id}:`, error);
    throw error;
  }
};

// Update budget
export const updateBudget = async (id, budgetData) => {
  try {
    const response = await api.put(`/budgets/${id}`, budgetData);
    return response.data;
  } catch (error) {
    console.error(`Failed to update budget ${id}:`, error);
    throw error;
  }
};

// Delete budget
export const deleteBudget = async (id) => {
  try {
    await api.delete(`/budgets/${id}`);
    return true;
  } catch (error) {
    console.error(`Failed to delete budget ${id}:`, error);
    throw error;
  }
};

// Get budget summary/overview
export const getBudgetSummary = async () => {
  try {
    const response = await api.get('/budgets/summary');
    return response.data;
  } catch (error) {
    console.error('Failed to fetch budget summary:', error);
    throw error;
  }
};

// Get budget by category
export const getBudgetByCategory = async (category) => {
  try {
    const response = await api.get(`/budgets/category/${category}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch budget for category ${category}:`, error);
    throw error;
  }
};
