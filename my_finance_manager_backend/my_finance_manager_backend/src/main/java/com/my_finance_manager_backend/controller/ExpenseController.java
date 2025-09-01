package com.my_finance_manager_backend.controller;

import com.my_finance_manager_backend.model.Expense;
import com.my_finance_manager_backend.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService service;

    public ExpenseController(ExpenseService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Expense> create(@RequestBody Expense expense) {
        return ResponseEntity.ok(service.save(expense));
    }

    // READ ALL (simple list)
    @GetMapping
    public ResponseEntity<List<Expense>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // READ ONE
    @GetMapping("/{id}")
    public ResponseEntity<Expense> getById(@PathVariable Long id) {
        Expense e = service.findById(id);
        return (e != null) ? ResponseEntity.ok(e) : ResponseEntity.notFound().build();
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Expense> update(@PathVariable Long id, @RequestBody Expense expense) {
        Expense existing = service.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        expense.setId(id);
        return ResponseEntity.ok(service.save(expense));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    // PAGINATION + FILTERING
    @GetMapping("/paged")
    public ResponseEntity<Page<Expense>> getPagedExpenses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Expense> result;

        if (startDate != null && endDate != null) {
            result = service.findByDateRange(startDate, endDate, pageable);
        } else {
            result = service.findAllPaged(pageable);
        }

        return ResponseEntity.ok(result);
    }
    // NEW ENDPOINT: Summary by Category
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Double>> getSummaryByCategory() {
        return ResponseEntity.ok(service.getSummaryByCategory());
    }

}
