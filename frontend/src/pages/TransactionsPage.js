import React, { useState, useEffect } from "react";
import { useTransactions } from "../contexts/TransactionContext";
import TransactionForm from "../components/transactions/TransactionForm";
import TransactionList from "../components/transactions/TransactionList";
import Button from "../components/common/Button";
import InputField from "../components/common/InputField";
import LoadingSpinner from "../components/common/LoadingSpinner";
import ErrorAlert from "../components/common/ErrorAlert";

export default function TransactionsPage() {
  const { 
    transactions, 
    loading, 
    error, 
    fetchTransactions,
    addTransaction,
    editTransaction,
    removeTransaction 
  } = useTransactions();
  
  const [showForm, setShowForm] = useState(false);
  const [editingTransaction, setEditingTransaction] = useState(null);
  const [filters, setFilters] = useState({
    startDate: "",
    endDate: "",
    type: "",
    category: ""
  });

  useEffect(() => {
    fetchTransactions(filters);
  }, [filters, fetchTransactions]);

  const handleCreateTransaction = async (transactionData) => {
    try {
      await addTransaction(transactionData);
      setShowForm(false);
    } catch (error) {
      console.error("Failed to create transaction:", error);
    }
  };

  const handleEditTransaction = async (id, transactionData) => {
    try {
      await editTransaction(id, transactionData);
      setEditingTransaction(null);
    } catch (error) {
      console.error("Failed to update transaction:", error);
    }
  };

  const handleDeleteTransaction = async (id) => {
    if (window.confirm("Are you sure you want to delete this transaction?")) {
      try {
        await removeTransaction(id);
      } catch (error) {
        console.error("Failed to delete transaction:", error);
      }
    }
  };

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({ ...prev, [field]: value }));
  };

  const clearFilters = () => {
    setFilters({
      startDate: "",
      endDate: "",
      type: "",
      category: ""
    });
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="transactions-page">
      <div className="page-header">
        <h1>Transactions</h1>
        <Button onClick={() => setShowForm(true)}>Add Transaction</Button>
      </div>

      {error && <ErrorAlert message={error} onClose={() => {}} />}

      {/* Filters */}
      <div className="filters-section">
        <h3>Filters</h3>
        <div className="filters-grid">
          <InputField
            type="date"
            label="Start Date"
            value={filters.startDate}
            onChange={(e) => handleFilterChange("startDate", e.target.value)}
          />
          <InputField
            type="date"
            label="End Date"
            value={filters.endDate}
            onChange={(e) => handleFilterChange("endDate", e.target.value)}
          />
          <InputField
            type="select"
            label="Type"
            value={filters.type}
            onChange={(e) => handleFilterChange("type", e.target.value)}
            options={[
              { value: "", label: "All" },
              { value: "income", label: "Income" },
              { value: "expense", label: "Expense" }
            ]}
          />
          <InputField
            type="text"
            label="Category"
            value={filters.category}
            onChange={(e) => handleFilterChange("category", e.target.value)}
            placeholder="Filter by category"
          />
        </div>
        <Button onClick={clearFilters} variant="secondary">Clear Filters</Button>
      </div>

      {/* Transaction Form */}
      {showForm && (
        <TransactionForm
          onSubmit={handleCreateTransaction}
          onCancel={() => setShowForm(false)}
        />
      )}

      {/* Edit Transaction Form */}
      {editingTransaction && (
        <TransactionForm
          transaction={editingTransaction}
          onSubmit={(data) => handleEditTransaction(editingTransaction.id, data)}
          onCancel={() => setEditingTransaction(null)}
          isEditing
        />
      )}

      {/* Transaction List */}
      <TransactionList
        transactions={transactions}
        onEdit={setEditingTransaction}
        onDelete={handleDeleteTransaction}
      />
    </div>
  );
}
