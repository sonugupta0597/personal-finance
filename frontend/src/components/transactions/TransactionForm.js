import React, { useState, useEffect } from "react";
import { format } from "date-fns";
import Button from "../common/Button";
import InputField from "../common/InputField";

const CATEGORIES = {
  income: [
    "Salary",
    "Freelance",
    "Investment",
    "Business",
    "Other"
  ],
  expense: [
    "Food & Dining",
    "Transportation",
    "Housing",
    "Utilities",
    "Entertainment",
    "Healthcare",
    "Shopping",
    "Education",
    "Travel",
    "Other"
  ]
};

export default function TransactionForm({ 
  transaction, 
  onSubmit, 
  onCancel, 
  isEditing = false 
}) {
  const [formData, setFormData] = useState({
    type: "expense",
    amount: "",
    description: "",
    category: "",
    date: format(new Date(), "yyyy-MM-dd"),
    notes: ""
  });

  useEffect(() => {
    if (transaction) {
      setFormData({
        type: transaction.type || "expense",
        amount: transaction.amount?.toString() || "",
        description: transaction.description || "",
        category: transaction.category || "",
        date: transaction.date ? format(new Date(transaction.date), "yyyy-MM-dd") : format(new Date(), "yyyy-MM-dd"),
        notes: transaction.notes || ""
      });
    }
  }, [transaction]);

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    
    // Reset category when type changes
    if (field === "type") {
      setFormData(prev => ({ ...prev, category: "" }));
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    
    if (!formData.amount || !formData.description || !formData.category) {
      alert("Please fill in all required fields");
      return;
    }

    const submitData = {
      ...formData,
      amount: parseFloat(formData.amount)
    };

    onSubmit(submitData);
  };

  return (
    <div className="transaction-form-overlay">
      <div className="transaction-form">
        <div className="form-header">
          <h3>{isEditing ? "Edit Transaction" : "Add Transaction"}</h3>
          <button className="close-btn" onClick={onCancel}>&times;</button>
        </div>

        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <InputField
              type="select"
              label="Type *"
              value={formData.type}
              onChange={(e) => handleChange("type", e.target.value)}
              required
              options={[
                { value: "income", label: "Income" },
                { value: "expense", label: "Expense" }
              ]}
            />
            
            <InputField
              type="number"
              label="Amount *"
              value={formData.amount}
              onChange={(e) => handleChange("amount", e.target.value)}
              placeholder="0.00"
              step="0.01"
              min="0"
              required
            />
          </div>

          <div className="form-row">
            <InputField
              type="text"
              label="Description *"
              value={formData.description}
              onChange={(e) => handleChange("description", e.target.value)}
              placeholder="Enter transaction description"
              required
            />
            
            <InputField
              type="select"
              label="Category *"
              value={formData.category}
              onChange={(e) => handleChange("category", e.target.value)}
              required
              options={[
                { value: "", label: "Select category" },
                ...CATEGORIES[formData.type].map(cat => ({
                  value: cat.toLowerCase().replace(/\s+/g, "-"),
                  label: cat
                }))
              ]}
            />
          </div>

          <div className="form-row">
            <InputField
              type="date"
              label="Date"
              value={formData.date}
              onChange={(e) => handleChange("date", e.target.value)}
            />
            
            <InputField
              type="text"
              label="Notes"
              value={formData.notes}
              onChange={(e) => handleChange("notes", e.target.value)}
              placeholder="Optional notes"
            />
          </div>

          <div className="form-actions">
            <Button type="submit" variant="primary">
              {isEditing ? "Update Transaction" : "Add Transaction"}
            </Button>
            <Button type="button" onClick={onCancel} variant="secondary">
              Cancel
            </Button>
          </div>
        </form>
      </div>
    </div>
  );
}
