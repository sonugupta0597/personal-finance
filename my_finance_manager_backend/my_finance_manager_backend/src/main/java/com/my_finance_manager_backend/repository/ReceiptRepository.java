package com.my_finance_manager_backend.repository;

import com.my_finance_manager_backend.model.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, Long> {
}
