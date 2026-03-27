package com.dollartracker.service;

import com.dollartracker.dto.ExpenseDto;
import com.dollartracker.entity.Expense;
import com.dollartracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    public Expense createExpense(String userId, ExpenseDto expenseDto) throws Exception {
        // Validate category
        if (expenseDto.getCategory() == null || expenseDto.getCategory().isEmpty()) {
            throw new Exception("Category is required");
        }

        // Validate amount
        if (expenseDto.getAmount() == null || expenseDto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Amount must be numeric and non-negative");
        }

        // Validate transaction date
        LocalDateTime transactionDateTime = parseTransactionDate(expenseDto.getTransactionDate());
        if (transactionDateTime.isAfter(LocalDateTime.now())) {
            throw new Exception("Future dates not allowed");
        }

        // Validate comments word count
        if (expenseDto.getComments() != null && !expenseDto.getComments().isEmpty()) {
            int wordCount = expenseDto.getComments().split("\\s+").length;
            if (wordCount > 100) {
                throw new Exception("Comments exceed 100 words");
            }
        }

        Expense expense = new Expense();
        expense.setUserId(userId);
        expense.setCategory(Expense.ExpenseCategory.valueOf(expenseDto.getCategory()));
        expense.setAmount(expenseDto.getAmount());
        expense.setComments(expenseDto.getComments());
        expense.setTransactionDate(transactionDateTime);
        expense.setIsRecurring(expenseDto.getIsRecurring() != null && expenseDto.getIsRecurring());
        if (expense.getIsRecurring() && expenseDto.getRecurringFrequency() != null) {
            expense.setRecurringFrequency(Expense.RecurringFrequency.valueOf(expenseDto.getRecurringFrequency()));
        }

        return expenseRepository.save(expense);
    }

    public Expense updateExpense(String userId, String expenseId, ExpenseDto expenseDto) throws Exception {
        Optional<Expense> expenseOptional = expenseRepository.findById(expenseId);
        if (!expenseOptional.isPresent() || !expenseOptional.get().getUserId().equals(userId)) {
            throw new Exception("Expense not found");
        }

        Expense expense = expenseOptional.get();

        if (expenseDto.getCategory() != null) {
            expense.setCategory(Expense.ExpenseCategory.valueOf(expenseDto.getCategory()));
        }

        if (expenseDto.getAmount() != null) {
            if (expenseDto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Amount must be non-negative");
            }
            expense.setAmount(expenseDto.getAmount());
        }

        if (expenseDto.getComments() != null && !expenseDto.getComments().isEmpty()) {
            int wordCount = expenseDto.getComments().split("\\s+").length;
            if (wordCount > 100) {
                throw new Exception("Comments exceed 100 words");
            }
            expense.setComments(expenseDto.getComments());
        }

        if (expenseDto.getTransactionDate() != null) {
            LocalDateTime transactionDateTime = parseTransactionDate(expenseDto.getTransactionDate());
            if (transactionDateTime.isAfter(LocalDateTime.now())) {
                throw new Exception("Future dates not allowed");
            }
            expense.setTransactionDate(transactionDateTime);
        }

        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByDateRange(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        return expenseRepository.findExpensesByDateRange(userId, startDate, endDate);
    }

    public List<Expense> getThisMonthExpenses(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thisMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return getExpensesByDateRange(userId, thisMonth, now);
    }

    public BigDecimal getTotalExpenses(String userId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Expense> expenses = expenseRepository.findExpensesByDateRange(userId, startDate, endDate);
        return expenses.stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getTotalExpensesByMonth(String userId, int year, int month) {
        LocalDateTime startDate = YearMonth.of(year, month).atDay(1).atStartOfDay();
        LocalDateTime endDate = YearMonth.of(year, month).atEndOfMonth().atTime(23, 59, 59);
        return getTotalExpenses(userId, startDate, endDate);
    }

    public List<Expense> getCurrentMonthExpenses(String userId) {
        YearMonth currentMonth = YearMonth.now();
        return expenseRepository.findExpensesByMonth(userId, currentMonth.getYear(), currentMonth.getMonthValue());
    }

    public List<Expense> getLast30DaysExpenses(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfMonth = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return expenseRepository.findExpensesByDateRange(userId, startOfMonth, now);
    }

    // public List<Expense> getLast7DaysExpenses(String userId) {
    //     LocalDateTime now = LocalDateTime.now();
    //     LocalDateTime sevenDaysAgo = now.minusDays(7);
    //     return expenseRepository.findExpensesByDateRange(userId, sevenDaysAgo, now);
    // }

    public List<Expense> getExpensesFromMonthStart(String userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime monthStart = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        return expenseRepository.findExpensesByDateRange(userId, monthStart, now);
    }

    public void deleteExpense(String userId, String expenseId) throws Exception {
        Optional<Expense> expenseOptional = expenseRepository.findById(expenseId);
        if (!expenseOptional.isPresent() || !expenseOptional.get().getUserId().equals(userId)) {
            throw new Exception("Expense not found");
        }
        expenseRepository.delete(expenseOptional.get());
    }

    private LocalDateTime parseTransactionDate(String dateStr) throws Exception {
        if (dateStr == null || dateStr.isEmpty()) {
            throw new Exception("Transaction date is required");
        }

        // Try MM/YY format
        if (dateStr.matches("^\\d{2}/\\d{2}$")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
                YearMonth yearMonth = YearMonth.parse(dateStr, formatter);
                return yearMonth.atDay(1).atStartOfDay();
            } catch (Exception e) {
                throw new Exception("Invalid date format. Use MM/YY or YYYY-MM-DD");
            }
        }

        // Try YYYY-MM-DD format
        if (dateStr.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                return LocalDateTime.parse(dateStr + "T00:00:00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            } catch (Exception e) {
                throw new Exception("Invalid date format");
            }
        }

        throw new Exception("Invalid date format. Use MM/YY or YYYY-MM-DD");
    }
}
