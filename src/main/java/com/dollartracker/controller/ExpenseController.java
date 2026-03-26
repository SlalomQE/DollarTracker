package com.dollartracker.controller;

import com.dollartracker.dto.ApiResponse;
import com.dollartracker.dto.ExpenseDto;
import com.dollartracker.entity.Expense;
import com.dollartracker.service.ExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/expenses")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping
    public ResponseEntity<ApiResponse> createExpense(@RequestBody ExpenseDto expenseDto, Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            Expense expense = expenseService.createExpense(userId, expenseDto);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(expense)
                    .message("Expense created successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @PutMapping("/{expenseId}")
    public ResponseEntity<ApiResponse> updateExpense(@PathVariable String expenseId, @RequestBody ExpenseDto expenseDto, Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            Expense expense = expenseService.updateExpense(userId, expenseId, expenseDto);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(expense)
                    .message("Expense updated successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse> searchExpenses(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = (endDate != null ? endDate : LocalDate.now()).atTime(23, 59, 59);
            
            List<Expense> expenses = expenseService.getExpensesByDateRange(userId, startDateTime, endDateTime);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(expenses)
                    .message("Expenses retrieved successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @GetMapping("/last-7-days")
    public ResponseEntity<ApiResponse> getLast7DaysExpenses(Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            List<Expense> expenses = expenseService.getLast7DaysExpenses(userId);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(expenses)
                    .message("Last 7 days expenses retrieved successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }

    @DeleteMapping("/{expenseId}")
    public ResponseEntity<ApiResponse> deleteExpense(@PathVariable String expenseId, Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            expenseService.deleteExpense(userId, expenseId);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Expense deleted successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
        }
    }
}
