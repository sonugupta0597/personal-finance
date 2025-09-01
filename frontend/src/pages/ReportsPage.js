
import React, { useState, useEffect, useCallback } from "react";
import { format, subMonths, startOfMonth, endOfMonth } from "date-fns";
import { useTransactions } from "../contexts/TransactionContext";
import ExpenseByCategoryChart from "../components/charts/ExpenseByCategoryChart";
import ExpenseByDateChart from "../components/charts/ExpenseByDateChart";
import IncomeVsExpenseChart from "../components/charts/IncomeVsExpenseChart";
import Button from "../components/common/Button";
import InputField from "../components/common/InputField";
import LoadingSpinner from "../components/common/LoadingSpinner";
import { getIncomeSummary, getExpenseSummary } from "../api";

export default function ReportsPage() {
  const { transactions, loading } = useTransactions();
  const [dateRange, setDateRange] = useState("3months");
  const [customStartDate, setCustomStartDate] = useState("");
  const [customEndDate, setCustomEndDate] = useState("");
  const [filteredTransactions, setFilteredTransactions] = useState([]);
  const [summary, setSummary] = useState({
    totalIncome: 0,
    totalExpenses: 0,
    netAmount: 0,
    transactionCount: 0
  });

  const fetchSummaryFromBackend = async (startDate, endDate) => {
    try {
      const [incomeSummary, expenseSummary] = await Promise.all([
        getIncomeSummary(startDate, endDate),
        getExpenseSummary(startDate, endDate)
      ]);

      // Update summary with backend data if available
      if (incomeSummary && expenseSummary) {
        const totalIncome = Object.values(incomeSummary).reduce((sum, amount) => sum + amount, 0);
        const totalExpenses = Object.values(expenseSummary).reduce((sum, amount) => sum + amount, 0);
        
        setSummary(prev => ({
          ...prev,
          totalIncome,
          totalExpenses,
          netAmount: totalIncome - totalExpenses
        }));
      }
    } catch (error) {
      console.error("Failed to fetch summary from backend:", error);
      // Fall back to local calculation
    }
  };

  const filterTransactions = useCallback(() => {
    let startDate, endDate;
    
    switch (dateRange) {
      case "1month":
        startDate = startOfMonth(subMonths(new Date(), 1));
        endDate = endOfMonth(new Date());
        break;
      case "3months":
        startDate = startOfMonth(subMonths(new Date(), 3));
        endDate = endOfMonth(new Date());
        break;
      case "6months":
        startDate = startOfMonth(subMonths(new Date(), 6));
        endDate = endOfMonth(new Date());
        break;
      case "1year":
        startDate = startOfMonth(subMonths(new Date(), 12));
        endDate = endOfMonth(new Date());
        break;
      case "custom":
        if (customStartDate && customEndDate) {
          startDate = new Date(customStartDate);
          endDate = new Date(customEndDate);
        } else {
          return;
        }
        break;
      default:
        startDate = startOfMonth(subMonths(new Date(), 3));
        endDate = endOfMonth(new Date());
    }

    const filtered = transactions.filter(transaction => {
      const transactionDate = new Date(transaction.date);
      return transactionDate >= startDate && transactionDate <= endDate;
    });

    setFilteredTransactions(filtered);
    calculateSummary(filtered);
    
    // Also fetch summary from backend for more accurate data
    fetchSummaryFromBackend(startDate, endDate);
  }, [dateRange, customStartDate, customEndDate, transactions]);

  useEffect(() => {
    if (transactions && transactions.length > 0) {
      filterTransactions();
    }
  }, [transactions, dateRange, customStartDate, customEndDate, filterTransactions]);

  const calculateSummary = (transactions) => {
    const income = transactions
      .filter(t => t.type === "income")
      .reduce((sum, t) => sum + t.amount, 0);
    
    const expenses = transactions
      .filter(t => t.type === "expense")
      .reduce((sum, t) => sum + t.amount, 0);

    setSummary({
      totalIncome: income,
      totalExpenses: expenses,
      netAmount: income - expenses,
      transactionCount: transactions.length
    });
  };

  const exportReport = () => {
    const reportData = {
      dateRange,
      summary,
      transactions: filteredTransactions,
      generatedAt: new Date().toISOString()
    };

    const blob = new Blob([JSON.stringify(reportData, null, 2)], {
      type: "application/json"
    });
    
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `financial-report-${format(new Date(), "yyyy-MM-dd")}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="reports-page">
      <div className="page-header">
        <h1>Financial Reports</h1>
        <p>Comprehensive analysis of your financial activity</p>
      </div>

      {/* Date Range Selection */}
      <div className="date-range-section">
        <h3>Select Date Range</h3>
        <div className="date-controls">
          <div className="preset-ranges">
            <Button
              onClick={() => setDateRange("1month")}
              variant={dateRange === "1month" ? "primary" : "secondary"}
              size="small"
            >
              Last Month
            </Button>
            <Button
              onClick={() => setDateRange("3months")}
              variant={dateRange === "3months" ? "primary" : "secondary"}
              size="small"
            >
              Last 3 Months
            </Button>
            <Button
              onClick={() => setDateRange("6months")}
              variant={dateRange === "6months" ? "primary" : "secondary"}
              size="small"
            >
              Last 6 Months
            </Button>
            <Button
              onClick={() => setDateRange("1year")}
              variant={dateRange === "1year" ? "primary" : "secondary"}
              size="small"
            >
              Last Year
            </Button>
            <Button
              onClick={() => setDateRange("custom")}
              variant={dateRange === "custom" ? "primary" : "secondary"}
              size="small"
            >
              Custom Range
            </Button>
          </div>

          {dateRange === "custom" && (
            <div className="custom-date-inputs">
              <InputField
                type="date"
                label="Start Date"
                value={customStartDate}
                onChange={(e) => setCustomStartDate(e.target.value)}
              />
              <InputField
                type="date"
                label="End Date"
                value={customEndDate}
                onChange={(e) => setCustomEndDate(e.target.value)}
              />
            </div>
          )}
        </div>
      </div>

      {/* Summary Cards */}
      <div className="summary-cards">
        <div className="summary-card income">
          <h4>Total Income</h4>
          <p className="amount">${summary.totalIncome.toFixed(2)}</p>
        </div>
        <div className="summary-card expense">
          <h4>Total Expenses</h4>
          <p className="amount">${summary.totalExpenses.toFixed(2)}</p>
        </div>
        <div className="summary-card net">
          <h4>Net Amount</h4>
          <p className={`amount ${summary.netAmount >= 0 ? 'positive' : 'negative'}`}>
            {summary.netAmount >= 0 ? '+' : ''}${summary.netAmount.toFixed(2)}
          </p>
        </div>
        <div className="summary-card count">
          <h4>Transactions</h4>
          <p className="amount">{summary.transactionCount}</p>
        </div>
      </div>

      {/* Charts */}
      <div className="charts-section">
        <h3>Financial Analysis</h3>
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
      </div>

      {/* Export Section */}
      <div className="export-section">
        <h3>Export Report</h3>
        <p>Download your financial report for the selected period</p>
        <Button onClick={exportReport} variant="primary">
          Export Report (JSON)
        </Button>
      </div>
    </div>
  );
}
