package com.dollartracker.repository;

import com.dollartracker.entity.Revenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RevenueRepository extends JpaRepository<Revenue, String> {
    List<Revenue> findByUserIdOrderByTransactionDateDesc(String userId);
    
    @Query("SELECT r FROM Revenue r WHERE r.userId = :userId AND YEAR(r.transactionDate) = :year AND MONTH(r.transactionDate) = :month ORDER BY r.transactionDate DESC")
    List<Revenue> findRevenuesByMonth(@Param("userId") String userId, @Param("year") int year, @Param("month") int month);
}
