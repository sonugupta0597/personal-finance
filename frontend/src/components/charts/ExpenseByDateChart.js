import React, { useState, useEffect } from "react";
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { useTransactions } from "../../contexts/TransactionContext";

export default function ExpenseByDateChart() {
  const { transactions } = useTransactions();
  const [chartData, setChartData] = useState([]);

  useEffect(() => {
    if (transactions && transactions.length > 0) {
      // Filter only expenses and group by date
      const expenses = transactions.filter(t => t.type === "expense");
      const dateTotals = {};
      
      expenses.forEach(expense => {
        const date = new Date(expense.date);
        const dateKey = date.toISOString().split('T')[0]; // YYYY-MM-DD format
        
        if (!dateTotals[dateKey]) {
          dateTotals[dateKey] = { date: dateKey, amount: 0 };
        }
        
        dateTotals[dateKey].amount += expense.amount;
      });

      // Convert to chart data format and sort by date
      const data = Object.values(dateTotals)
        .sort((a, b) => new Date(a.date) - new Date(b.date))
        .map(item => ({
          date: new Date(item.date).toLocaleDateString("en-US", { 
            month: "short", 
            day: "numeric" 
          }),
          amount: parseFloat(item.amount.toFixed(2))
        }));

      setChartData(data);
    }
  }, [transactions]);

  if (!chartData || chartData.length === 0) {
    return (
      <div className="chart">
        <h3>Expenses Over Time</h3>
        <p>No expense data available</p>
      </div>
    );
  }

  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <div className="custom-tooltip">
          <p className="label">{`Date: ${label}`}</p>
          <p style={{ color: "#FF8042" }}>
            {`Amount: $${payload[0].value}`}
          </p>
        </div>
      );
    }
    return null;
  };

  return (
    <div className="chart">
      <h3>Expenses Over Time</h3>
      <ResponsiveContainer width="100%" height={300}>
        <LineChart data={chartData}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="date" />
          <YAxis />
          <Tooltip content={<CustomTooltip />} />
          <Legend />
          <Line 
            type="monotone" 
            dataKey="amount" 
            stroke="#FF8042" 
            strokeWidth={2}
            dot={{ fill: "#FF8042", strokeWidth: 2, r: 4 }}
            activeDot={{ r: 6 }}
            name="Expense Amount"
          />
        </LineChart>
      </ResponsiveContainer>
    </div>
  );
}
