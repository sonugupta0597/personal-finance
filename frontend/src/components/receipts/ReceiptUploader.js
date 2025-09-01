import React, { useState } from "react";
import Button from "../common/Button";
import LoadingSpinner from "../common/LoadingSpinner";
import { uploadReceipt, createTransactionFromReceipt } from "../../api/transactions";

export default function ReceiptUploader() {
  const [selectedFiles, setSelectedFiles] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [extractedData, setExtractedData] = useState(null);
  const [error, setError] = useState("");

  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    const imageFiles = files.filter(file => 
      file.type.startsWith('image/') && 
      ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'].includes(file.type)
    );
    
    if (imageFiles.length !== files.length) {
      setError('Please select only image files (JPEG, PNG, WebP)');
      return;
    }
    
    setSelectedFiles(imageFiles);
    setExtractedData(null);
    setError("");
  };

  const handleUpload = async () => {
    if (selectedFiles.length === 0) {
      setError('Please select files to upload');
      return;
    }

    setUploading(true);
    setUploadProgress(0);
    setError("");

    try {
      // Simulate upload progress
      const progressInterval = setInterval(() => {
        setUploadProgress(prev => {
          if (prev >= 90) {
            clearInterval(progressInterval);
            return 90;
          }
          return prev + 10;
        });
      }, 200);

      // Process each file individually
      const allExtractedData = [];
      
      for (const file of selectedFiles) {
        try {
          // Make actual API call to backend for receipt processing
          const response = await uploadReceipt(file);
          
          if (response && response.length > 0) {
            allExtractedData.push(...response);
          }
        } catch (fileError) {
          console.error(`Failed to process file ${file.name}:`, fileError);
          // Continue with other files
        }
      }
      
      clearInterval(progressInterval);
      setUploadProgress(100);

      // Process extracted data
      if (allExtractedData.length > 0) {
        const firstTransaction = allExtractedData[0];
        const extractedData = {
          totalAmount: firstTransaction.amount || 0,
          merchant: firstTransaction.merchant || "Unknown",
          date: firstTransaction.date || new Date().toISOString(),
          items: firstTransaction.items || [],
          category: firstTransaction.category || "general",
          description: firstTransaction.description || "Receipt upload",
          type: "expense", // Default to expense for receipts
          rawData: allExtractedData
        };
        setExtractedData(extractedData);
      } else {
        throw new Error("No transactions extracted from receipts");
      }
      
      // Reset files after successful upload
      setSelectedFiles([]);
      
    } catch (error) {
      console.error('Upload failed:', error);
      setError(error.message || 'Upload failed. Please try again.');
    } finally {
      setUploading(false);
      setUploadProgress(0);
    }
  };

  const handleCreateTransaction = async () => {
    if (!extractedData) return;

    try {
      setUploading(true);
      
      // Prepare transaction data
      const transactionData = {
        type: extractedData.type,
        amount: extractedData.totalAmount,
        description: extractedData.description,
        category: extractedData.category,
        date: extractedData.date,
        merchant: extractedData.merchant,
        items: extractedData.items,
        source: "receipt_upload"
      };

      // Create the transaction
      const createdTransaction = await createTransactionFromReceipt(transactionData);
      
      if (createdTransaction) {
        alert('Transaction created successfully!');
        setExtractedData(null);
        // Optionally refresh the transactions list or redirect
      }
    } catch (error) {
      console.error('Failed to create transaction:', error);
      setError('Failed to create transaction. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const removeFile = (index) => {
    setSelectedFiles(prev => prev.filter((_, i) => i !== index));
    setError("");
  };

  return (
    <div className="receipt-uploader">
      <div className="upload-area">
        <input
          type="file"
          multiple
          accept="image/*"
          onChange={handleFileSelect}
          style={{ display: 'none' }}
          id="receipt-file-input"
        />
        
        <label htmlFor="receipt-file-input" className="file-input-label">
          <div className="upload-icon">📷</div>
          <p>Click to select receipt images</p>
          <p className="file-types">Supported: JPEG, PNG, WebP</p>
        </label>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {selectedFiles.length > 0 && (
        <div className="selected-files">
          <h4>Selected Files ({selectedFiles.length})</h4>
          <div className="file-list">
            {selectedFiles.map((file, index) => (
              <div key={index} className="file-item">
                <span className="file-name">{file.name}</span>
                <span className="file-size">
                  {(file.size / 1024 / 1024).toFixed(2)} MB
                </span>
                <Button
                  onClick={() => removeFile(index)}
                  variant="danger"
                  size="small"
                >
                  Remove
                </Button>
              </div>
            ))}
          </div>
          
          <Button
            onClick={handleUpload}
            disabled={uploading}
            className="upload-btn"
          >
            {uploading ? 'Processing...' : 'Process Receipts'}
          </Button>
        </div>
      )}

      {uploading && (
        <div className="upload-progress">
          <LoadingSpinner />
          <p>Processing receipts... {uploadProgress}%</p>
          <div className="progress-bar">
            <div 
              className="progress-fill" 
              style={{ width: `${uploadProgress}%` }}
            ></div>
          </div>
        </div>
      )}

      {extractedData && (
        <div className="extracted-data">
          <h4>Extracted Information</h4>
          <div className="data-summary">
            <div className="data-item">
              <span className="label">Total Amount:</span>
              <span className="value">${extractedData.totalAmount}</span>
            </div>
            <div className="data-item">
              <span className="label">Merchant:</span>
              <span className="value">{extractedData.merchant}</span>
            </div>
            <div className="data-item">
              <span className="label">Date:</span>
              <span className="value">
                {new Date(extractedData.date).toLocaleDateString()}
              </span>
            </div>
            <div className="data-item">
              <span className="label">Category:</span>
              <span className="value">{extractedData.category}</span>
            </div>
          </div>
          
          {extractedData.items && extractedData.items.length > 0 && (
            <div className="extracted-items">
              <h5>Items:</h5>
              <ul>
                {extractedData.items.map((item, index) => (
                  <li key={index}>
                    {item.name}: ${item.price}
                  </li>
                ))}
              </ul>
            </div>
          )}
          
          <div className="extracted-actions">
            <Button 
              onClick={handleCreateTransaction} 
              variant="primary"
              disabled={uploading}
            >
              {uploading ? 'Creating...' : 'Create Transaction'}
            </Button>
            <Button onClick={() => setExtractedData(null)} variant="secondary">
              Process Another Receipt
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
