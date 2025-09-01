import React, { useState, useEffect } from "react";
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { useTransactions } from "../../contexts/TransactionContext";

export default function IncomeVsExpenseChart() {
  const { transactions } = useTransactions();
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    if (transactions && transactions.length > 0) {
      // Group transactions by month
      const monthlyData = {};
      
      transactions.forEach(transaction => {
        const date = new Date(transaction.date);
        const monthKey = `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}`;
        
        if (!monthlyData[monthKey]) {
          monthlyData[monthKey] = { month: monthKey, income: 0, expense: 0 };
        }
        
        if (transaction.type === "income") {
          monthlyData[monthKey].income += transaction.amount;
        } else {
          monthlyData[monthKey].expense += transaction.amount;
        }
      });

      // Convert to chart data format and sort by month
      const data = Object.values(monthlyData)
        .sort((a, b) => a.month.localeCompare(b.month))
        .map(item => ({
          month: new Date(item.month + "-01").toLocaleDateString("en-US", { 
            month: "short", 
            year: "numeric" 
          }),
          income: parseFloat(item.income.toFixed(2)),
          expense: parseFloat(item.expense.toFixed(2))
        }));

      setChartData(data);
    }
  }, [transactions]);

  if (!chartData || chartData.length === 0) {
    return (
      <div className="chart">
        <h3>Income vs Expenses</h3>
        <p>No transaction data available</p>
      </div>
    );
  }

  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="custom-tooltip">
          <p className="label">{`Month: ${label}`}</p>
          {payload.map((entry, index) => (
            <p key={index} style={{ color: entry.color }}>
              {`${entry.name}: $${entry.value}`}
            </p>
          ))}
        </div>
      );
    }
    return null;
  };

  return (
    <div className="chart">
      <h3>Income vs Expenses</h3>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip content={<CustomTooltip />} />
          <Legend />
          <Bar dataKey="income" fill="#00C49F" name="Income" />
          <Bar dataKey="expense" fill="#FF8042" name="Expense" />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
