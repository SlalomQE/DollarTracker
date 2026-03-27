package com.dollartracker.repository;

import com.dollartracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String> {
    List<Expense> findByUserIdOrderByTransactionDateDesc(String userId);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.transactionDate BETWEEN :startDate AND :endDate ORDER BY e.transactionDate DESC")
    List<Expense> findExpensesByDateRange(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND e.transactionDate >= :startDate AND e.transactionDate < :endDate ORDER BY e.transactionDate DESC")
    List<Expense> findLast30Days(@Param("userId") String userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Expense e WHERE e.userId = :userId AND YEAR(e.transactionDate) = :year AND MONTH(e.transactionDate) = :month ORDER BY e.transactionDate DESC")
    List<Expense> findExpensesByMonth(@Param("userId") String userId, @Param("year") int year, @Param("month") int month);
}
