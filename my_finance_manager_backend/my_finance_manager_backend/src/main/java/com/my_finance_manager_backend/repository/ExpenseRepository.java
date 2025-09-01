package com.my_finance_manager_backend.repository;

import com.my_finance_manager_backend.model.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    // Existing (pagination + filtering handled in Service)
    org.springframework.data.domain.Page<Expense> findByDateBetween(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            org.springframework.data.domain.Pageable pageable
    );

    // NEW: Group expenses by category with total amounts
    @Query("SELECT e.category, SUM(e.amount) FROM Expense e GROUP BY e.category")
    List<Object[]> getExpenseSummaryByCategory();
}
