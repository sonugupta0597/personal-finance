package com.my_finance_manager_backend.service;

import com.my_finance_manager_backend.model.Expense;
import com.my_finance_manager_backend.repository.ExpenseRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Service
public class ExpenseService {
    private final ExpenseRepository repo;

    public ExpenseService(ExpenseRepository repo) {
        this.repo = repo;
    }

    // CREATE / UPDATE
    public Expense save(Expense expense) {
        return repo.save(expense);
    }

    // READ all (list)
    public List<Expense> findAll() {
        return repo.findAll();
    }

    // READ single
    public Expense findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // DELETE
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Pagination only
    public Page<Expense> findAllPaged(Pageable pageable) {
        return repo.findAll(pageable);
    }

    // Pagination + Date filtering
    public Page<Expense> findByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return repo.findByDateBetween(start, end, pageable);
    }
    public Map<String, Double> getSummaryByCategory() {
        Map<String, Double> summary = new HashMap<>();
        List<Object[]> results = repo.getExpenseSummaryByCategory();
        for (Object[] row : results) {
            String category = (String) row[0];
            Double total = ((Number) row[1]).doubleValue();
            summary.put(category, total);
        }
        return summary;
    }
}
