import api from './axios';

// Upload receipt file and extract transactions using the new bill scan API
export const uploadReceipt = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    // Use the new bill scan endpoint
    const response = await api.post('/bill-scan/scan', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    // Transform the response to match expected format
    const scanResult = response.data;
    
    // Convert the scan result to transaction format
    const transaction = {
      amount: scanResult.amount || 0,
      merchant: scanResult.merchantName || "Unknown",
      date: scanResult.transactionDate || new Date().toISOString(),
      items: scanResult.items || [],
      category: scanResult.category || "general",
      description: scanResult.description || "Receipt scan",
      type: "expense", // Default to expense for receipts
      receiptNumber: scanResult.invoiceNumber || "N/A",
      taxAmount: scanResult.taxAmount || 0,
      currency: scanResult.currency || "USD",
      totalAmount: scanResult.totalAmount || scanResult.amount || 0,
      paymentMethod: scanResult.paymentMethod || "Unknown",
      aiConfidence: scanResult.aiConfidence || "Unknown",
      rawData: scanResult
    };
    
    return [transaction]; // Return as array to maintain compatibility
  } catch (error) {
    console.error('Failed to upload receipt:', error);
    throw error;
  }
};

// Upload statement file and extract transactions using the new bill scan API
export const uploadStatement = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    // Use the new bill scan endpoint for statements too
    const response = await api.post('/bill-scan/scan', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    // Transform the response to match expected format
    const scanResult = response.data;
    
    // Convert the scan result to transaction format
    const transaction = {
      amount: scanResult.amount || 0,
      merchant: scanResult.merchantName || "Unknown",
      date: scanResult.transactionDate || new Date().toISOString(),
      items: scanResult.items || [],
      category: scanResult.category || "general",
      description: scanResult.description || "Statement scan",
      type: "expense", // Default to expense for statements
      receiptNumber: scanResult.invoiceNumber || "N/A",
      taxAmount: scanResult.taxAmount || 0,
      currency: scanResult.currency || "USD",
      totalAmount: scanResult.totalAmount || scanResult.amount || 0,
      paymentMethod: scanResult.paymentMethod || "Unknown",
      aiConfidence: scanResult.aiConfidence || "Unknown",
      rawData: scanResult
    };
    
    return [transaction]; // Return as array to maintain compatibility
  } catch (error) {
    console.error('Failed to upload statement:', error);
    throw error;
  }
};

// Create transaction from extracted receipt data
export const createTransactionFromReceipt = async (transactionData) => {
  try {
    const response = await api.post('/transactions/save', [transactionData]);
    return response;
  } catch (error) {
    console.error('Failed to create transaction from receipt:', error);
    throw error;
  }
};

// Save extracted transactions to database
export const saveTransactions = async (transactions) => {
  try {
    const response = await api.post('/transactions/save', transactions);
    return response;
  } catch (error) {
    console.error('Failed to save transactions:', error);
    throw error;
  }
};

// Get transactions with pagination and optional date filtering
export const getTransactions = async (page = 0, size = 10, startDate = null, endDate = null) => {
  try {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
    });

    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await api.get(`/transactions?${params}`);
    return response;
  } catch (error) {
    console.error('Failed to fetch transactions:', error);
    throw error;
  }
};

// Get transaction summary
export const getTransactionSummary = async (startDate = null, endDate = null) => {
  try {
    const params = new URLSearchParams();
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    const response = await api.get(`/transactions/summary?${params}`);
    return response;
  } catch (error) {
    console.error('Failed to fetch transaction summary:', error);
    throw error;
  }
};

// Get transaction by ID
export const getTransactionById = async (id) => {
  try {
    const response = await api.get(`/transactions/${id}`);
    return response;
  } catch (error) {
    console.error(`Failed to fetch transaction ${id}:`, error);
    throw error;
  }
};

// Update transaction
export const updateTransaction = async (id, transactionData) => {
  try {
    const response = await api.put(`/transactions/${id}`, transactionData);
    return response;
  } catch (error) {
    console.error(`Failed to update transaction ${id}:`, error);
    throw error;
  }
};

// Delete transaction
export const deleteTransaction = async (id) => {
  try {
    await api.delete(`/transactions/${id}`);
    return true;
  } catch (error) {
    console.error(`Failed to delete transaction ${id}:`, error);
    throw error;
  }
};

// Scan bill/receipt using the new Gemini AI endpoint
export const scanBill = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);

    const response = await api.post('/bill-scan/scan', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    
    return response.data; // Return raw scan result
  } catch (error) {
    console.error('Failed to scan bill:', error);
    throw error;
  }
};

// Get bill scan service information
export const getBillScanInfo = async () => {
  try {
    const response = await api.get('/bill-scan/info');
    return response.data;
  } catch (error) {
    console.error('Failed to get bill scan info:', error);
    throw error;
  }
};

// Check bill scan service health
export const checkBillScanHealth = async () => {
  try {
    const response = await api.get('/bill-scan/health');
    return response.data;
  } catch (error) {
    console.error('Failed to check bill scan health:', error);
    throw error;
  }
};