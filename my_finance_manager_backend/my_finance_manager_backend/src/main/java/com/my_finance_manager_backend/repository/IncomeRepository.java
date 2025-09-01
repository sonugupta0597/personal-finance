package com.my_finance_manager_backend.repository;

import com.my_finance_manager_backend.model.Income;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface IncomeRepository extends JpaRepository<Income, Long> {

    Page<Income> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // NEW: Group incomes by source with total amounts
    @Query("SELECT i.source, SUM(i.amount) FROM Income i GROUP BY i.source")
    List<Object[]> getIncomeSummaryBySource();
}
