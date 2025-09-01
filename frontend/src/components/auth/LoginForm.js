import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";
import Button from "../common/Button";
import InputField from "../common/InputField";
import LoadingSpinner from "../common/LoadingSpinner";

export default function LoginForm() {
  const [formData, setFormData] = useState({
    email: "",
    password: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  
  const { login } = useAuth();
  const navigate = useNavigate();

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setError(""); // Clear error when user types
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.email || !formData.password) {
      setError("Please fill in all fields");
      return;
    }

    setLoading(true);
    setError("");

    try {
      await login(formData.email, formData.password);
      navigate("/dashboard");
    } catch (error) {
      setError(error.message || "Login failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="login-form">
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      <InputField
        type="email"
        label="Email"
        value={formData.email}
        onChange={(e) => handleChange("email", e.target.value)}
        placeholder="Enter your email"
        required
      />

      <InputField
        type="password"
        label="Password"
        value={formData.password}
        onChange={(e) => handleChange("password", e.target.value)}
        placeholder="Enter your password"
        required
      />

      <Button
        type="submit"
        disabled={loading}
        className="submit-btn"
        variant="primary"
      >
        {loading ? <LoadingSpinner size="small" text="" /> : "Login"}
      </Button>
    </form>
  );
}
