import React, { useState, useEffect } from "react";
import { PieChart, Pie, Cell, ResponsiveContainer, Tooltip, Legend } from "recharts";
import { useTransactions } from "../../contexts/TransactionContext";

const COLORS = [
  "#0088FE", "#00C49F", "#FFBB28", "#FF8042", "#8884D8",
  "#82CA9D", "#FFC658", "#FF6B6B", "#4ECDC4", "#45B7D1"
];

export default function ExpenseByCategoryChart() {
  const { transactions } = useTransactions();
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    if (transactions && transactions.length > 0) {
      // Filter only expenses and group by category
      const expenses = transactions.filter(t => t.type === "expense");
      const categoryTotals = {};
      
      expenses.forEach(expense => {
        const category = expense.category || "Uncategorized";
        categoryTotals[category] = (categoryTotals[category] || 0) + expense.amount;
      });

      // Convert to chart data format
      const data = Object.entries(categoryTotals).map(([category, amount], index) => ({
        name: category,
        value: parseFloat(amount.toFixed(2)),
        color: COLORS[index % COLORS.length]
      }));

      setChartData(data);
    }
  }, [transactions]);

  if (!chartData || chartData.length === 0) {
    return (
      <div className="chart">
        <h3>Expenses by Category</h3>
        <p>No expense data available</p>
      </div>
    );
  }

  const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
      return (
        <div className="custom-tooltip">
          <p className="label">{`${payload[0].name} : $${payload[0].value}`}</p>
        </div>
      );
    }
    return null;
  };

  return (
    <div className="chart">
      <h3>Expenses by Category</h3>
      <ResponsiveContainer width="100%" height={300}>
        <PieChart>
          <Pie
            data={chartData}
            cx="50%"
            cy="50%"
            labelLine={false}
            label={({ name, percent }) => `${name} ${(percent * 100).toFixed(0)}%`}
            outerRadius={80}
            fill="#8884d8"
            dataKey="value"
          >
            {chartData.map((entry, index) => (
              <Cell key={`cell-${index}`} fill={entry.color} />
            ))}
          </Pie>
          <Tooltip content={<CustomTooltip />} />
          <Legend />
        </PieChart>
      </ResponsiveContainer>
    </div>
  );
}
