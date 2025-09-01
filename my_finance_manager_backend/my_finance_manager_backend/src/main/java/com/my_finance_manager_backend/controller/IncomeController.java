package com.my_finance_manager_backend.controller;

import com.my_finance_manager_backend.model.Income;
import com.my_finance_manager_backend.service.IncomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/incomes")
public class IncomeController {

    private final IncomeService service;

    public IncomeController(IncomeService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Income> create(@RequestBody Income income) {
        return ResponseEntity.ok(service.save(income));
    }

    // READ all
    @GetMapping
    public ResponseEntity<List<Income>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // READ one
    @GetMapping("/{id}")
    public ResponseEntity<Income> getById(@PathVariable Long id) {
        Income i = service.findById(id);
        return (i != null) ? ResponseEntity.ok(i) : ResponseEntity.notFound().build();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Income> update(@PathVariable Long id, @RequestBody Income income) {
        Income existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        income.setId(id);
        return ResponseEntity.ok(service.save(income));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // PAGINATION + FILTERING
    @GetMapping("/paged")
    public ResponseEntity<Page<Income>> getPagedIncomes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Income> result;

        if (startDate != null && endDate != null) {
            result = service.findByDateRange(startDate, endDate, pageable);
        } else {
            result = service.findAllPaged(pageable);
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummaryBySource() {
        return ResponseEntity.ok(service.getSummaryBySource());
    }
}
