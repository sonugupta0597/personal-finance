import React, { useState } from "react";
import { useAuth } from "../../hooks/useAuth";
import Button from "../common/Button";
import InputField from "../common/InputField";
import LoadingSpinner from "../common/LoadingSpinner";

export default function RegisterForm() {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
    confirmPassword: ""
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  
  const { register } = useAuth();

  const handleChange = (field, value) => {
    setFormData(prev => ({ ...prev, [field]: value }));
    setError(""); // Clear error when user types
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.username || !formData.email || !formData.password || !formData.confirmPassword) {
      setError("Please fill in all fields");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match");
      return;
    }

    if (formData.password.length < 6) {
      setError("Password must be at least 6 characters long");
      return;
    }

    setLoading(true);
    setError("");
    setSuccess("");

    try {
      await register({
        username: formData.username,
        email: formData.email,
        password: formData.password
      });
      
      setSuccess("Registration successful! Please log in.");
      setFormData({
        username: "",
        email: "",
        password: "",
        confirmPassword: ""
      });
    } catch (error) {
      setError(error.message || "Registration failed. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="register-form">
      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {success && (
        <div className="success-message">
          {success}
        </div>
      )}

      <InputField
        type="text"
        label="Username"
        value={formData.username}
        onChange={(e) => handleChange("username", e.target.value)}
        placeholder="Enter your username"
        required
      />

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

      <InputField
        type="password"
        label="Confirm Password"
        value={formData.confirmPassword}
        onChange={(e) => handleChange("confirmPassword", e.target.value)}
        placeholder="Confirm your password"
        required
      />

      <Button
        type="submit"
        disabled={loading}
        className="submit-btn"
        variant="primary"
      >
        {loading ? <LoadingSpinner size="small" text="" /> : "Register"}
      </Button>
    </form>
  );
}
