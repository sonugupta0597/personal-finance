import api from './axios';

// Get incomes with pagination and optional date filtering
export const getIncomes = async (page = 0, size = 10, startDate = null, endDate = null) => {
  try {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await api.get(`/incomes/paged?${params}`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch incomes:', error);
    throw error;
  }
};

// Get all incomes (simple list)
export const getAllIncomes = async () => {
  try {
    const response = await api.get('/incomes');
    return response.data;
  } catch (error) {
    console.error('Failed to fetch all incomes:', error);
    throw error;
  }
};

// Get income by ID
export const getIncomeById = async (id) => {
  try {
    const response = await api.get(`/incomes/${id}`);
    return response.data;
  } catch (error) {
    console.error(`Failed to fetch income ${id}:`, error);
    throw error;
  }
};

// Add new income
export const addIncome = async (incomeData) => {
  try {
    const response = await api.post('/incomes', incomeData);
    return response.data;
  } catch (error) {
    console.error('Failed to add income:', error);
    throw error;
  }
};

// Update existing income
export const updateIncome = async (id, incomeData) => {
  try {
    const response = await api.put(`/incomes/${id}`, incomeData);
    return response.data;
  } catch (error) {
    console.error(`Failed to update income ${id}:`, error);
    throw error;
  }
};

// Delete income
export const deleteIncome = async (id) => {
  try {
    await api.delete(`/incomes/${id}`);
    return true;
  } catch (error) {
    console.error(`Failed to delete income ${id}:`, error);
    throw error;
  }
};

// Get income summary by source
export const getIncomeSummary = async (startDate = null, endDate = null) => {
  try {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await api.get(`/incomes/summary?${params}`);
    return response.data;
  } catch (error) {
    console.error('Failed to fetch income summary:', error);
    throw error;
  }
};
