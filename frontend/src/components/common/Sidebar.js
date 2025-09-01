import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

export default function Sidebar() {
  const location = useLocation();
  const { user } = useAuth();

  const navigationItems = [
    { path: "/dashboard", label: "Dashboard", icon: "📊" },
    { path: "/transactions", label: "Transactions", icon: "💰" },
    { path: "/reports", label: "Reports", icon: "📈" },
    { path: "/receipts", label: "Receipts", icon: "🧾" }
  ];

  if (!user) return null;

  return (
    <aside className="sidebar">
      <nav className="sidebar-nav">
        <ul className="nav-list">
          {navigationItems.map((item) => (
            <li key={item.path} className="nav-item">
              <Link
                to={item.path}
                className={`nav-link ${location.pathname === item.path ? "active" : ""}`}
              >
                <span className="nav-icon">{item.icon}</span>
                <span className="nav-label">{item.label}</span>
              </Link>
            </li>
          ))}
        </ul>
      </nav>
    </aside>
  );
}
