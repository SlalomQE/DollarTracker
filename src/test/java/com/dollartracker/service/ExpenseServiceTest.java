package com.dollartracker.service;

import com.dollartracker.dto.ExpenseDto;
import com.dollartracker.entity.Expense;
import com.dollartracker.repository.ExpenseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseService expenseService;

    private ExpenseDto expenseDto;
    private Expense testExpense;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        expenseDto = new ExpenseDto();
        expenseDto.setCategory("GROCERIES");
        expenseDto.setAmount(BigDecimal.valueOf(50.00));
        expenseDto.setTransactionDate("2026-03-24");
        expenseDto.setComments("Weekly groceries");

        testExpense = new Expense();
        testExpense.setExpenseId(UUID.randomUUID().toString());
        testExpense.setUserId(userId);
        testExpense.setCategory(Expense.ExpenseCategory.GROCERIES);
        testExpense.setAmount(BigDecimal.valueOf(50.00));
        testExpense.setTransactionDate(LocalDateTime.now());
        testExpense.setComments("Weekly groceries");
    }

    @Test
    void testCreateExpenseSuccess() throws Exception {
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        Expense result = expenseService.createExpense(userId, expenseDto);

        assertNotNull(result);
        assertEquals("GROCERIES", result.getCategory().toString());
        assertEquals(BigDecimal.valueOf(50.00), result.getAmount());
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testCreateExpenseNullCategory() {
        expenseDto.setCategory(null);

        Exception exception = assertThrows(Exception.class, () -> {
            expenseService.createExpense(userId, expenseDto);
        });

        assertEquals("Category is required", exception.getMessage());
    }

    @Test
    void testCreateExpenseNegativeAmount() {
        expenseDto.setAmount(BigDecimal.valueOf(-10.00));

        Exception exception = assertThrows(Exception.class, () -> {
            expenseService.createExpense(userId, expenseDto);
        });

        assertTrue(exception.getMessage().contains("non-negative"));
    }

    @Test
    void testCreateExpenseExceedCommentLimit() {
        StringBuilder comments = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            comments.append("word ");
        }
        expenseDto.setComments(comments.toString());

        Exception exception = assertThrows(Exception.class, () -> {
            expenseService.createExpense(userId, expenseDto);
        });

        assertTrue(exception.getMessage().contains("exceed"));
    }

    @Test
    void testUpdateExpenseSuccess() throws Exception {
        Optional<Expense> optionalExpense = Optional.of(testExpense);
        when(expenseRepository.findById(testExpense.getExpenseId())).thenReturn(optionalExpense);
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        Expense result = expenseService.updateExpense(userId, testExpense.getExpenseId(), expenseDto);

        assertNotNull(result);
        verify(expenseRepository, times(1)).save(any(Expense.class));
    }

    @Test
    void testUpdateExpenseNotFound() {
        when(expenseRepository.findById("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            expenseService.updateExpense(userId, "invalidId", expenseDto);
        });

        assertEquals("Expense not found", exception.getMessage());
    }

    @Test
    void testUpdateExpenseUnauthorized() {
        String otherUserId = UUID.randomUUID().toString();
        testExpense.setUserId(otherUserId);
        Optional<Expense> optionalExpense = Optional.of(testExpense);
        when(expenseRepository.findById(testExpense.getExpenseId())).thenReturn(optionalExpense);

        Exception exception = assertThrows(Exception.class, () -> {
            expenseService.updateExpense(userId, testExpense.getExpenseId(), expenseDto);
        });

        assertEquals("Expense not found", exception.getMessage());
    }

    @Test
    void testGetExpensesByDateRange() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(testExpense);

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        LocalDateTime endDate = LocalDateTime.now();

        when(expenseRepository.findExpensesByDateRange(userId, startDate, endDate))
                .thenReturn(expenses);

        List<Expense> result = expenseService.getExpensesByDateRange(userId, startDate, endDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(expenseRepository, times(1)).findExpensesByDateRange(userId, startDate, endDate);
    }

    @Test
    void testGetLast30DaysExpenses() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(testExpense);

        when(expenseRepository.findExpensesByDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expenses);

        List<Expense> result = expenseService.getLast30DaysExpenses(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTotalExpensesByMonth() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(testExpense);

        when(expenseRepository.findExpensesByDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expenses);

        BigDecimal total = expenseService.getTotalExpensesByMonth(userId, 2026, 3);

        assertEquals(BigDecimal.valueOf(50.00), total);
    }

    @Test
    void testGetCurrentMonthExpenses() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(testExpense);

        when(expenseRepository.findExpensesByMonth(userId, YearMonth.now().getYear(), YearMonth.now().getMonthValue()))
                .thenReturn(expenses);

        List<Expense> result = expenseService.getCurrentMonthExpenses(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetExpensesFromMonthStart() {
        List<Expense> expenses = new ArrayList<>();
        expenses.add(testExpense);

        when(expenseRepository.findExpensesByDateRange(anyString(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(expenses);

        List<Expense> result = expenseService.getExpensesFromMonthStart(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testDeleteExpenseSuccess() throws Exception {
        Optional<Expense> optionalExpense = Optional.of(testExpense);
        when(expenseRepository.findById(testExpense.getExpenseId())).thenReturn(optionalExpense);

        expenseService.deleteExpense(userId, testExpense.getExpenseId());

        verify(expenseRepository, times(1)).delete(testExpense);
    }

    @Test
    void testDeleteExpenseNotFound() {
        when(expenseRepository.findById("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            expenseService.deleteExpense(userId, "invalidId");
        });

        assertEquals("Expense not found", exception.getMessage());
    }
}
