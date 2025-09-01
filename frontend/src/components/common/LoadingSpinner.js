import React from "react";

export default function LoadingSpinner({ size = "medium", text = "Loading..." }) {
  const sizeClasses = {
    small: "spinner-small",
    medium: "spinner-medium",
    large: "spinner-large"
  };

  return (
    <div className="loading-spinner">
      <div className={`spinner ${sizeClasses[size] || sizeClasses.medium}`}></div>
      {text && <p className="loading-text">{text}</p>}
    </div>
  );
}
