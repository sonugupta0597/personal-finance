import React from "react";

export default function InputField({
  type = "text",
  label,
  value,
  onChange,
  placeholder = "",
  required = false,
  disabled = false,
  options = [],
  className = "",
  ...props
}) {
  const renderInput = () => {
    switch (type) {
      case "select":
        return (
          <select
            value={value}
            onChange={onChange}
            disabled={disabled}
            required={required}
            className="input-field"
            {...props}
          >
            {options.map((option, index) => (
              <option key={index} value={option.value}>
                {option.label}
              </option>
            ))}
          </select>
        );
      
      case "textarea":
        return (
          <textarea
            value={value}
            onChange={onChange}
            placeholder={placeholder}
            disabled={disabled}
            required={required}
            className="input-field"
            {...props}
          />
        );
      
      default:
        return (
          <input
            type={type}
            value={value}
            onChange={onChange}
            placeholder={placeholder}
            disabled={disabled}
            required={required}
            className="input-field"
            {...props}
          />
        );
    }
  };

  return (
    <div className={`input-field-container ${className}`}>
      {label && (
        <label className="input-label">
          {label}
          {required && <span className="required">*</span>}
        </label>
      )}
      {renderInput()}
    </div>
  );
}
