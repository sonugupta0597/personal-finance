import React from "react";
import ExpenseByCategoryChart from "../components/charts/ExpenseByCategoryChart";
import ExpenseByDateChart from "../components/charts/ExpenseByDateChart";
import IncomeVsExpenseChart from "../components/charts/IncomeVsExpenseChart";
import TransactionList from "../components/transactions/TransactionList";

export default function DashboardPage() {
  const handleEdit = (transaction) => {
    // For dashboard, we'll redirect to transactions page for editing
    window.location.href = '/transactions';
  };

  const handleDelete = (id) => {
    // For dashboard, we'll redirect to transactions page for deletion
    window.location.href = '/transactions';
  };

  return (
    <div className="dashboard">
      <div className="page-header">
        <h1>Dashboard</h1>
        <p>Overview of your financial activity</p>
      </div>
      
      <div className="charts-grid">
        <div className="chart-container">
          <ExpenseByCategoryChart />
        </div>
        <div className="chart-container">
          <IncomeVsExpenseChart />
        </div>
        <div className="chart-container full-width">
          <ExpenseByDateChart />
        </div>
      </div>
      
      <div className="recent-transactions">
        <TransactionList 
          limit={5} 
          onEdit={handleEdit}
          onDelete={handleDelete}
        />
      </div>
    </div>
  );
}
