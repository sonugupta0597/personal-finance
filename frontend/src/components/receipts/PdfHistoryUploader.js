
import React, { useState } from "react";
import Button from "../common/Button";
import LoadingSpinner from "../common/LoadingSpinner";
import { scanBill, createTransactionFromReceipt } from "../../api/transactions";

export default function PdfHistoryUploader() {
  const [selectedPdfs, setSelectedPdfs] = useState([]);
  const [uploading, setUploading] = useState(false);
  const [uploadProgress, setUploadProgress] = useState(0);
  const [extractedData, setExtractedData] = useState(null);
  const [error, setError] = useState("");

  const handleFileSelect = (event) => {
    const files = Array.from(event.target.files);
    const pdfFiles = files.filter(file => file.type === 'application/pdf');
    
    if (pdfFiles.length !== files.length) {
      setError('Please select only PDF files');
      return;
    }
    
    setSelectedPdfs(pdfFiles);
    setExtractedData(null);
    setError("");
  };

  const handleUpload = async () => {
    if (selectedPdfs.length === 0) {
      setError('Please select PDF files to upload');
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
      }, 300);

      // Process each PDF individually using the new bill scan API
      const allExtractedData = [];
      
      for (const file of selectedPdfs) {
        try {
          // Use the new bill scan API
          const scanResult = await scanBill(file);
          
          if (scanResult && (scanResult.merchantName || scanResult.amount > 0)) {
            // Transform the scan result to match expected format
            const transaction = {
              amount: scanResult.amount || 0,
              merchant: scanResult.merchantName || "Unknown",
              date: scanResult.transactionDate || new Date().toISOString(),
              items: scanResult.items || [],
              category: scanResult.category || "general",
              description: scanResult.description || "PDF receipt scan",
              type: "expense", // Default to expense for receipts
              receiptNumber: scanResult.invoiceNumber || "N/A",
              taxAmount: scanResult.taxAmount || 0,
              currency: scanResult.currency || "USD",
              totalAmount: scanResult.totalAmount || scanResult.amount || 0,
              paymentMethod: scanResult.paymentMethod || "Unknown",
              aiConfidence: scanResult.aiConfidence || "Unknown",
              rawData: scanResult
            };
            
            allExtractedData.push(transaction);
          }
        } catch (fileError) {
          console.error(`Failed to process PDF ${file.name}:`, fileError);
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
          description: firstTransaction.description || "PDF receipt upload",
          type: "expense", // Default to expense for receipts
          receiptNumber: firstTransaction.receiptNumber || "N/A",
          taxAmount: firstTransaction.taxAmount || 0,
          currency: firstTransaction.currency || "USD",
          paymentMethod: firstTransaction.paymentMethod || "Unknown",
          aiConfidence: firstTransaction.aiConfidence || "Unknown",
          rawData: allExtractedData
        };
        setExtractedData(extractedData);
      } else {
        throw new Error("No transactions extracted from PDFs");
      }
      
      // Reset files after successful upload
      setSelectedPdfs([]);
      
    } catch (error) {
      console.error('PDF upload failed:', error);
      setError(error.message || 'PDF upload failed. Please try again.');
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
        receiptNumber: extractedData.receiptNumber,
        taxAmount: extractedData.taxAmount,
        currency: extractedData.currency,
        paymentMethod: extractedData.paymentMethod,
        aiConfidence: extractedData.aiConfidence,
        source: "pdf_receipt_upload"
      };

      // Create the transaction
      const createdTransaction = await createTransactionFromReceipt(transactionData);
      
      if (createdTransaction) {
        alert('Transaction created successfully from PDF!');
        setExtractedData(null);
        // Optionally refresh the transactions list or redirect
      }
    } catch (error) {
      console.error('Failed to create transaction from PDF:', error);
      setError('Failed to create transaction. Please try again.');
    } finally {
      setUploading(false);
    }
  };

  const removeFile = (index) => {
    setSelectedPdfs(prev => prev.filter((_, i) => i !== index));
    setError("");
  };

  return (
    <div className="pdf-uploader">
      <div className="upload-area">
        <input
          type="file"
          multiple
          accept=".pdf"
          onChange={handleFileSelect}
          style={{ display: 'none' }}
          id="pdf-file-input"
        />
        
        <label htmlFor="pdf-file-input" className="file-input-label">
          <div className="upload-icon">📄</div>
          <p>Click to select PDF receipts</p>
          <p className="file-types">Supported: PDF files</p>
        </label>
      </div>

      {error && (
        <div className="error-message">
          {error}
        </div>
      )}

      {selectedPdfs.length > 0 && (
        <div className="selected-files">
          <h4>Selected PDFs ({selectedPdfs.length})</h4>
          <div className="file-list">
            {selectedPdfs.map((file, index) => (
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
            {uploading ? 'Processing PDFs...' : 'Process PDF Receipts'}
          </Button>
        </div>
      )}

      {uploading && (
        <div className="upload-progress">
          <LoadingSpinner />
          <p>Processing PDF receipts... {uploadProgress}%</p>
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
          <h4>Extracted Information from PDF</h4>
          <div className="data-summary">
            <div className="data-item">
              <span className="label">Total Amount:</span>
              <span className="value">{extractedData.currency} {extractedData.totalAmount}</span>
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
            {extractedData.receiptNumber && extractedData.receiptNumber !== "N/A" && (
              <div className="data-item">
                <span className="label">Receipt #:</span>
                <span className="value">{extractedData.receiptNumber}</span>
              </div>
            )}
            {extractedData.taxAmount > 0 && (
              <div className="data-item">
                <span className="label">Tax:</span>
                <span className="value">{extractedData.currency} {extractedData.taxAmount}</span>
              </div>
            )}
            {extractedData.paymentMethod && extractedData.paymentMethod !== "Unknown" && (
              <div className="data-item">
                <span className="label">Payment Method:</span>
                <span className="value">{extractedData.paymentMethod}</span>
              </div>
            )}
            {extractedData.aiConfidence && extractedData.aiConfidence !== "Unknown" && (
              <div className="data-item">
                <span className="label">AI Confidence:</span>
                <span className="value">{extractedData.aiConfidence}</span>
              </div>
            )}
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
              Process Another PDF
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
