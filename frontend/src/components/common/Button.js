import React from "react";

export default function Button({ 
  children, 
  onClick, 
  type = "button", 
  variant = "primary", 
  size = "medium",
  disabled = false,
  className = "",
  ...props 
}) {
  const baseClasses = "btn";
  const variantClasses = {
    primary: "btn-primary",
    secondary: "btn-secondary",
    danger: "btn-danger",
    success: "btn-success",
    warning: "btn-warning"
  };
  
  const sizeClasses = {
    small: "btn-small",
    medium: "btn-medium",
    large: "btn-large"
  };

  const classes = [
    baseClasses,
    variantClasses[variant] || variantClasses.primary,
    sizeClasses[size] || sizeClasses.medium,
    className
  ].filter(Boolean).join(" ");

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={classes}
      {...props}
    >
      {children}
    </button>
  );
}
