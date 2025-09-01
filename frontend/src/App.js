import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider } from "./contexts/AuthContext";
import { TransactionProvider } from "./contexts/TransactionContext";
import { useAuth } from "./hooks/useAuth";

import AuthPage from "./pages/AuthPage";
import DashboardPage from "./pages/DashboardPage";
import TransactionsPage from "./pages/TransactionsPage";
import ReportsPage from "./pages/ReportsPage";
import ReceiptsPage from "./pages/ReceiptsPage";

import Header from "./components/common/Header";
import Footer from "./components/common/Footer";
import Sidebar from "./components/common/Sidebar";

// Protected Route Component
function ProtectedRoute({ children }) {
  const { user, ready } = useAuth();
  
  if (!ready) {
    return <div>Loading...</div>;
  }
  
  if (!user) {
    return <Navigate to="/auth" replace />;
  }
  
  return children;
}

// Public Route Component (redirects authenticated users)
function PublicRoute({ children }) {
  const { user, ready } = useAuth();
  
  if (!ready) {
    return <div>Loading...</div>;
  }
  
  if (user) {
    return <Navigate to="/dashboard" replace />;
  }
  
  return children;
}

function AppContent() {
  return (
    <div className="layout">
      <Sidebar />
      <div className="main">
        <Header />
        <div className="page">
          <Routes>
            <Route path="/auth" element={
              <PublicRoute>
                <AuthPage />
              </PublicRoute>
            } />
            <Route path="/dashboard" element={
              <ProtectedRoute>
                <DashboardPage />
              </ProtectedRoute>
            } />
            <Route path="/transactions" element={
              <ProtectedRoute>
                <TransactionsPage />
              </ProtectedRoute>
            } />
            <Route path="/reports" element={
              <ProtectedRoute>
                <ReportsPage />
              </ProtectedRoute>
            } />
            <Route path="/receipts" element={
              <ProtectedRoute>
                <ReceiptsPage />
              </ProtectedRoute>
            } />
            <Route path="*" element={<Navigate to="/auth" replace />} />
          </Routes>
        </div>
        <Footer />
      </div>
    </div>
  );
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <TransactionProvider>
          <AppContent />
        </TransactionProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;