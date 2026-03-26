package com.dollartracker.service;

import com.dollartracker.dto.RevenueDto;
import com.dollartracker.entity.Revenue;
import com.dollartracker.repository.RevenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class RevenueService {

    @Autowired
    private RevenueRepository revenueRepository;

    public Revenue createRevenue(String userId, RevenueDto revenueDto) throws Exception {
        // Validate source
        if (revenueDto.getSource() == null || revenueDto.getSource().isEmpty()) {
            throw new Exception("Revenue source is required");
        }

        // Validate amount
        if (revenueDto.getAmount() == null || revenueDto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("Amount must be numeric and non-negative");
        }

        // Validate transaction date
        LocalDateTime transactionDateTime = parseTransactionDate(revenueDto.getTransactionDate());

        // Validate notes word count
        if (revenueDto.getNotes() != null && !revenueDto.getNotes().isEmpty()) {
            int wordCount = revenueDto.getNotes().split("\\s+").length;
            if (wordCount > 100) {
                throw new Exception("Notes exceed 100 words");
            }
        }

        Revenue revenue = new Revenue();
        revenue.setUserId(userId);
        revenue.setSource(Revenue.RevenueSource.valueOf(revenueDto.getSource()));
        revenue.setAmount(revenueDto.getAmount());
        revenue.setTransactionDate(transactionDateTime);
        revenue.setNotes(revenueDto.getNotes());
        revenue.setIsRecurring(revenueDto.getIsRecurring() != null && revenueDto.getIsRecurring());
        
        if (revenue.getIsRecurring() && revenueDto.getRecurringFrequency() != null) {
            revenue.setRecurringFrequency(Revenue.RecurringFrequency.valueOf(revenueDto.getRecurringFrequency()));
        }

        return revenueRepository.save(revenue);
    }

    public Revenue updateRevenue(String userId, String revenueId, RevenueDto revenueDto) throws Exception {
        Optional<Revenue> revenueOptional = revenueRepository.findById(revenueId);
        if (!revenueOptional.isPresent() || !revenueOptional.get().getUserId().equals(userId)) {
            throw new Exception("Revenue not found");
        }

        Revenue revenue = revenueOptional.get();

        if (revenueDto.getSource() != null) {
            revenue.setSource(Revenue.RevenueSource.valueOf(revenueDto.getSource()));
        }

        if (revenueDto.getAmount() != null) {
            if (revenueDto.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                throw new Exception("Amount must be non-negative");
            }
            revenue.setAmount(revenueDto.getAmount());
        }

        if (revenueDto.getTransactionDate() != null) {
            LocalDateTime transactionDateTime = parseTransactionDate(revenueDto.getTransactionDate());
            revenue.setTransactionDate(transactionDateTime);
        }

        if (revenueDto.getNotes() != null) {
            int wordCount = revenueDto.getNotes().split("\\s+").length;
            if (wordCount > 100) {
                throw new Exception("Notes exceed 100 words");
            }
            revenue.setNotes(revenueDto.getNotes());
        }

        return revenueRepository.save(revenue);
    }

    public List<Revenue> getRevenuesByMonth(String userId, int year, int month) {
        return revenueRepository.findRevenuesByMonth(userId, year, month);
    }

    public List<Revenue> getCurrentMonthRevenues(String userId) {
        YearMonth currentMonth = YearMonth.now();
        return revenueRepository.findRevenuesByMonth(userId, currentMonth.getYear(), currentMonth.getMonthValue());
    }

    public BigDecimal getTotalRevenue(String userId, int year, int month) {
        List<Revenue> revenues = revenueRepository.findRevenuesByMonth(userId, year, month);
        return revenues.stream()
                .map(Revenue::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void deleteRevenue(String userId, String revenueId) throws Exception {
        Optional<Revenue> revenueOptional = revenueRepository.findById(revenueId);
        if (!revenueOptional.isPresent() || !revenueOptional.get().getUserId().equals(userId)) {
            throw new Exception("Revenue not found");
        }
        revenueRepository.delete(revenueOptional.get());
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
