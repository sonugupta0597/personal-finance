import React, { useState } from "react";

export default function ErrorAlert({ message, onClose, autoClose = true }) {
  const [isVisible, setIsVisible] = useState(true);

  React.useEffect(() => {
    if (autoClose) {
      const timer = setTimeout(() => {
        setIsVisible(false);
        if (onClose) onClose();
      }, 5000);

      return () => clearTimeout(timer);
    }
  }, [autoClose, onClose]);

  const handleClose = () => {
    setIsVisible(false);
    if (onClose) onClose();
  };

  if (!isVisible) return null;

  return (
    <div className="error-alert">
      <div className="error-content">
        <span className="error-icon">⚠️</span>
        <span className="error-message">{message}</span>
      </div>
      <button className="error-close" onClick={handleClose}>
        ×
      </button>
    </div>
  );
}
