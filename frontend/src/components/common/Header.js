import React from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import Button from "./Button";

export default function Header() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = async () => {
    try {
      const result = await logout();
      if (result.success) {
        // Navigate to auth page after successful logout
        navigate("/auth", { replace: true });
      }
    } catch (error) {
      console.error("Logout failed:", error);
      // Even if logout fails, redirect to auth page
      navigate("/auth", { replace: true });
    }
  };

  return (
    <header className="header">
      <div className="header-content">
        <div className="header-left">
          <h2 className="app-title">Personal-Finance-Manager</h2>
        </div>
        
        <div className="header-right">
          {user ? (
            <div className="user-section">
              <span className="user-name">Welcome, {user.username || user.email}</span>
              <Button onClick={handleLogout} variant="secondary" size="small">
                Logout
              </Button>
            </div>
          ) : (
            <div className="auth-section">
              <span>Please log in to continue</span>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
