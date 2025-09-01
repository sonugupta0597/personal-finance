import React, { createContext, useState, useEffect, useContext, useCallback } from "react";
import { getExpenses, getIncomes, addExpense, addIncome, updateExpense, updateIncome, deleteExpense, deleteIncome } from "../api";
import { useAuth } from "../hooks/useAuth";

export const TransactionContext = createContext();

export function TransactionProvider({ children }) {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [refreshKey, setRefreshKey] = useState(0);
  const { user } = useAuth();

  const fetchTransactions = useCallback(async (filters = {}) => {
    if (!user) return;
    
    setLoading(true);
    setError(null);
    try {
      // Fetch both incomes and expenses
      const [incomes, expenses] = await Promise.all([
        getIncomes(filters.page || 0, filters.limit || 10, filters.startDate, filters.endDate),
        getExpenses(filters.page || 0, filters.limit || 10, filters.startDate, filters.endDate)
      ]);
      
      // Combine and format data
      const combined = [
        ...incomes.content.map(income => ({
          ...income,
          type: 'income',
          amount: income.amount,
          category: income.source,
          date: income.date
        })),
        ...expenses.content.map(expense => ({
          ...expense,
          type: 'expense',
          amount: expense.amount,
          category: expense.category,
          date: expense.date
        }))
      ];
      
      // Filter by type if specified
      let filtered = combined;
      if (filters.type) {
        filtered = combined.filter(t => t.type === filters.type);
      }
      
      // Filter by category if specified
      if (filters.category) {
        filtered = filtered.filter(t => t.category === filters.category);
      }
      
      setTransactions(filtered);
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [user]);

  const addTransaction = async (transactionData) => {
    try {
      let newTransaction;
      if (transactionData.type === 'income') {
        newTransaction = await addIncome({
          amount: transactionData.amount,
          source: transactionData.category,
          description: transactionData.description,
          date: transactionData.date
        });
      } else {
        newTransaction = await addExpense({
          amount: transactionData.amount,
          category: transactionData.category,
          description: transactionData.description,
          date: transactionData.date
        });
      }
      
      // Add type to the transaction for frontend consistency
      newTransaction.type = transactionData.type;
      newTransaction.category = transactionData.type === 'income' ? newTransaction.source : newTransaction.category;
      
      setTransactions(prev => [newTransaction, ...prev]);
      return newTransaction;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const editTransaction = async (id, transactionData) => {
    try {
      let updatedTransaction;
      if (transactionData.type === 'income') {
        updatedTransaction = await updateIncome(id, {
          amount: transactionData.amount,
          source: transactionData.category,
          description: transactionData.description,
          date: transactionData.date
        });
      } else {
        updatedTransaction = await updateExpense(id, {
          amount: transactionData.amount,
          category: transactionData.category,
          description: transactionData.description,
          date: transactionData.date
        });
      }
      
      // Add type to the transaction for frontend consistency
      updatedTransaction.type = transactionData.type;
      updatedTransaction.category = transactionData.type === 'income' ? updatedTransaction.source : updatedTransaction.category;
      
      setTransactions(prev => 
        prev.map(t => t.id === id ? updatedTransaction : t)
      );
      return updatedTransaction;
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const removeTransaction = async (id, type) => {
    try {
      if (type === 'income') {
        await deleteIncome(id);
      } else {
        await deleteExpense(id);
      }
      setTransactions(prev => prev.filter(t => t.id !== id));
    } catch (err) {
      setError(err.message);
      throw err;
    }
  };

  const triggerRefresh = () => setRefreshKey(k => k + 1);

  useEffect(() => {
    if (user) {
      fetchTransactions();
    }
  }, [user, refreshKey, fetchTransactions]);

  const value = {
    transactions,
    loading,
    error,
    refreshKey,
    fetchTransactions,
    addTransaction,
    editTransaction,
    removeTransaction,
    triggerRefresh
  };

  return (
    <TransactionContext.Provider value={value}>
      {children}
    </TransactionContext.Provider>
  );
}

export const useTransactions = () => {
  const context = useContext(TransactionContext);
  if (!context) {
    throw new Error("useTransactions must be used within a TransactionProvider");
  }
  return context;
};