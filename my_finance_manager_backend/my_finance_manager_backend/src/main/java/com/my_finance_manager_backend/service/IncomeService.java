package com.my_finance_manager_backend.service;

import com.my_finance_manager_backend.model.Income;
import com.my_finance_manager_backend.repository.IncomeRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;

import java.util.Map;

@Service
public class IncomeService {
    private final IncomeRepository repo;

    public IncomeService(IncomeRepository repo) {
        this.repo = repo;
    }

    // CREATE / UPDATE
    public Income save(Income income) {
        return repo.save(income);
    }

    // READ all
    public List<Income> findAll() {
        return repo.findAll();
    }

    // READ one
    public Income findById(Long id) {
        return repo.findById(id).orElse(null);
    }

    // DELETE
    public void delete(Long id) {
        repo.deleteById(id);
    }

    // Pagination
    public Page<Income> findAllPaged(Pageable pageable) {
        return repo.findAll(pageable);
    }

    // Filtering
    public Page<Income> findByDateRange(LocalDate start, LocalDate end, Pageable pageable) {
        return repo.findByDateBetween(start, end, pageable);
    }
    public Map<String, Double> getSummaryBySource() {
        Map<String, Double> summary = new HashMap<>();
        List<Object[]> results = repo.getIncomeSummaryBySource();
        for (Object[] row : results) {
            String source = (String) row[0];
            Double total = ((Number) row[1]).doubleValue();
            summary.put(source, total);
        }
        return summary;
    }
}
