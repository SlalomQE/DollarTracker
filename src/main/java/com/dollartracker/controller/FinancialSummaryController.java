package com.dollartracker.controller;

import com.dollartracker.dto.ApiResponse;
import com.dollartracker.entity.Expense;
import com.dollartracker.entity.Revenue;
import com.dollartracker.service.ExpenseService;
import com.dollartracker.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/financial-summary")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FinancialSummaryController {

    @Autowired
    private RevenueService revenueService;

    @Autowired
    private ExpenseService expenseService;

    @GetMapping("/current-month")
    public ResponseEntity<ApiResponse> getCurrentMonthSummary(Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            YearMonth currentMonth = YearMonth.now();
            
            // Get totals
            BigDecimal totalRevenue = revenueService.getTotalRevenue(userId, currentMonth.getYear(), currentMonth.getMonthValue());
            BigDecimal totalExpenses = expenseService.getTotalExpensesByMonth(userId, currentMonth.getYear(), currentMonth.getMonthValue());
            BigDecimal remainingFunds = totalRevenue.subtract(totalExpenses);
            
            // Get transaction lists
            List<Revenue> revenues = revenueService.getCurrentMonthRevenues(userId);
            List<Expense> expenses = expenseService.getCurrentMonthExpenses(userId);
            
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalRevenue", totalRevenue);
            summary.put("totalExpenses", totalExpenses);
            summary.put("remainingFunds", remainingFunds);
            summary.put("month", currentMonth.toString());
            summary.put("expenses", expenses);
            summary.put("revenues", revenues);
            summary.put("expenseCount", expenses.size());
            summary.put("revenueCount", revenues.size());
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(summary)
                    .message("Financial summary retrieved successfully")
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(false)
                    .error(e.getMessage())
                    .timestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")))
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
}
