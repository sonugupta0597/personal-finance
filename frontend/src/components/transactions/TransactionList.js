import React from "react";
import TransactionItem from "./TransactionItem";

export default function TransactionList({ 
  transactions = [], 
  onEdit, 
  onDelete,
  limit 
}) {
  if (!transactions || transactions.length === 0) {
    return (
      <div className="transaction-list empty">
        <p>No transactions found. Add your first transaction to get started!</p>
      </div>
    );
  }

  const displayTransactions = limit ? transactions.slice(0, limit) : transactions;

  return (
    <div className="transaction-list">
      <div className="list-header">
        <h3>{limit ? `Recent Transactions` : `All Transactions (${transactions.length})`}</h3>
      </div>
      
      <div className="transactions-grid">
        {displayTransactions.map((transaction) => (
          <TransactionItem
            key={transaction.id}
            transaction={transaction}
            onEdit={() => onEdit(transaction)}
            onDelete={() => onDelete(transaction.id)}
          />
        ))}
      </div>
      
      {limit && transactions.length > limit && (
        <div className="view-all">
          <p>Showing {limit} of {transactions.length} transactions</p>
        </div>
      )}
    </div>
  );
}
