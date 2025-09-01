import React, { createContext, useState, useEffect } from "react";
import { loginApi, registerApi, meApi, logoutApi } from "../api/auth";
import { saveToken, getToken, clearToken } from "../utils/authUtils";

export const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [bootstrapped, setBootstrapped] = useState(false);

  useEffect(() => {
    (async () => {
      const token = getToken();
      if (!token) return setBootstrapped(true);
      try {
        const me = await meApi();
        setUser(me);
      } catch (error) {
        console.error("Failed to get user info:", error);
        // If token is invalid, clear it
        clearToken();
      } finally {
        setBootstrapped(true);
      }
    })();
  }, []);

  const login = async (email, password) => {
    const { token, user } = await loginApi(email, password);
    saveToken(token);
    setUser(user);
    return { success: true };
  };

  const register = async (payload) => {
    return registerApi(payload);
  };

  const logout = async () => {
    try {
      // Clear any pending API calls or state
      setUser(null);
      
      // Call logout API (if available)
      await logoutApi();
      
      // Clear all stored data
      clearToken();
      
      // Clear any other stored data (transactions, etc.)
      localStorage.removeItem('finance_manager_transactions');
      localStorage.removeItem('finance_manager_filters');
      
      console.log("Logout successful");
      return { success: true };
    } catch (error) {
      console.error("Logout error:", error);
      // Even if API call fails, clear local data
      clearToken();
      localStorage.removeItem('finance_manager_transactions');
      localStorage.removeItem('finance_manager_filters');
      setUser(null);
      return { success: true, error: error.message };
    }
  };

  return (
    <AuthContext.Provider value={{ user, login, register, logout, ready: bootstrapped }}>
      {children}
    </AuthContext.Provider>
  );
}