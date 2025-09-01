import React from "react";
import { format } from "date-fns";
import Button from "../common/Button";

export default function TransactionItem({ transaction, onEdit, onDelete }) {
  const { type, amount, description, category, date, notes } = transaction;
  
  const isIncome = type === "income";
  const amountClass = isIncome ? "amount income" : "amount expense";
  const amountPrefix = isIncome ? "+" : "-";

  return (
    <div className="transaction-item">
      <div className="transaction-header">
        <div className="transaction-type">
          <span className={`type-badge ${type}`}>
            {type === "income" ? "Income" : "Expense"}
          </span>
        </div>
        <div className="transaction-amount">
          <span className={amountClass}>
            {amountPrefix}${Math.abs(amount).toFixed(2)}
          </span>
        </div>
      </div>

      <div className="transaction-content">
        <h4 className="transaction-description">{description}</h4>
        
        <div className="transaction-details">
          <div className="detail-item">
            <span className="label">Category:</span>
            <span className="value">{category}</span>
          </div>
          
          <div className="detail-item">
            <span className="label">Date:</span>
            <span className="value">
              {date ? format(new Date(date), "MMM dd, yyyy") : "N/A"}
            </span>
          </div>
          
          {notes && (
            <div className="detail-item">
              <span className="label">Notes:</span>
              <span className="value">{notes}</span>
            </div>
          )}
        </div>
      </div>

      <div className="transaction-actions">
        <Button 
          onClick={onEdit} 
          variant="secondary" 
          size="small"
        >
          Edit
        </Button>
        <Button 
          onClick={onDelete} 
          variant="danger" 
          size="small"
        >
          Delete
        </Button>
      </div>
    </div>
  );
}
