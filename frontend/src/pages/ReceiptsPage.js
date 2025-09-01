import React, { useState } from "react";
import ReceiptUploader from "../components/receipts/ReceiptUploader";
import PdfHistoryUploader from "../components/receipts/PdfHistoryUploader";


export default function ReceiptsPage() {
  const [activeTab, setActiveTab] = useState("upload");

  return (
    <div className="receipts-page">
      <div className="page-header">
        <h1>Receipt Processing</h1>
        <p>Upload receipts to automatically extract expense information</p>
      </div>

      <div className="tabs">
        <button
          className={`tab ${activeTab === "upload" ? "active" : ""}`}
          onClick={() => setActiveTab("upload")}
        >
          Upload Receipts
        </button>
        <button
          className={`tab ${activeTab === "history" ? "active" : ""}`}
          onClick={() => setActiveTab("history")}
        >
          Processing History
        </button>
      </div>

      <div className="tab-content">
        {activeTab === "upload" && (
          <div className="upload-section">
            <h2>Upload Receipts</h2>
            <p>Upload images or PDFs of your receipts to automatically extract expense details.</p>
            
            <div className="upload-options">
              <div className="upload-option">
                <h3>Image Receipts</h3>
                <ReceiptUploader />
              </div>
              
              <div className="upload-option">
                <h3>PDF Receipts</h3>
                <PdfHistoryUploader />
              </div>
            </div>
          </div>
        )}

        {activeTab === "history" && (
          <div className="history-section">
            <h2>Processing History</h2>
            <p>View the history of processed receipts and extracted transactions.</p>
            
            <div className="history-list">
              <p>Processing history will be displayed here once receipts are uploaded and processed.</p>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
