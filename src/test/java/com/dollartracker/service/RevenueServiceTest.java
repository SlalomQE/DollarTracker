package com.dollartracker.service;

import com.dollartracker.dto.RevenueDto;
import com.dollartracker.entity.Revenue;
import com.dollartracker.repository.RevenueRepository;
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
class RevenueServiceTest {

    @Mock
    private RevenueRepository revenueRepository;

    @InjectMocks
    private RevenueService revenueService;

    private RevenueDto revenueDto;
    private Revenue testRevenue;
    private String userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID().toString();

        revenueDto = new RevenueDto();
        revenueDto.setSource("SALARY");
        revenueDto.setAmount(BigDecimal.valueOf(5000.00));
        revenueDto.setTransactionDate("2026-03-24");
        revenueDto.setNotes("Monthly salary");

        testRevenue = new Revenue();
        testRevenue.setRevenueId(UUID.randomUUID().toString());
        testRevenue.setUserId(userId);
        testRevenue.setSource(Revenue.RevenueSource.SALARY);
        testRevenue.setAmount(BigDecimal.valueOf(5000.00));
        testRevenue.setTransactionDate(LocalDateTime.now());
        testRevenue.setNotes("Monthly salary");
    }

    @Test
    void testCreateRevenueSuccess() throws Exception {
        when(revenueRepository.save(any(Revenue.class))).thenReturn(testRevenue);

        Revenue result = revenueService.createRevenue(userId, revenueDto);

        assertNotNull(result);
        assertEquals("SALARY", result.getSource().toString());
        assertEquals(BigDecimal.valueOf(5000.00), result.getAmount());
        verify(revenueRepository, times(1)).save(any(Revenue.class));
    }

    @Test
    void testCreateRevenueNullSource() {
        revenueDto.setSource(null);

        Exception exception = assertThrows(Exception.class, () -> {
            revenueService.createRevenue(userId, revenueDto);
        });

        assertEquals("Revenue source is required", exception.getMessage());
    }

    @Test
    void testCreateRevenueNegativeAmount() {
        revenueDto.setAmount(BigDecimal.valueOf(-100.00));

        Exception exception = assertThrows(Exception.class, () -> {
            revenueService.createRevenue(userId, revenueDto);
        });

        assertTrue(exception.getMessage().contains("non-negative"));
    }

    @Test
    void testCreateRevenueExceedNotesLimit() {
        StringBuilder notes = new StringBuilder();
        for (int i = 0; i < 101; i++) {
            notes.append("word ");
        }
        revenueDto.setNotes(notes.toString());

        Exception exception = assertThrows(Exception.class, () -> {
            revenueService.createRevenue(userId, revenueDto);
        });

        assertTrue(exception.getMessage().contains("exceed"));
    }

    @Test
    void testUpdateRevenueSuccess() throws Exception {
        Optional<Revenue> optionalRevenue = Optional.of(testRevenue);
        when(revenueRepository.findById(testRevenue.getRevenueId())).thenReturn(optionalRevenue);
        when(revenueRepository.save(any(Revenue.class))).thenReturn(testRevenue);

        Revenue result = revenueService.updateRevenue(userId, testRevenue.getRevenueId(), revenueDto);

        assertNotNull(result);
        verify(revenueRepository, times(1)).save(any(Revenue.class));
    }

    @Test
    void testUpdateRevenueNotFound() {
        when(revenueRepository.findById("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            revenueService.updateRevenue(userId, "invalidId", revenueDto);
        });

        assertEquals("Revenue not found", exception.getMessage());
    }

    @Test
    void testGetRevenuesByMonth() {
        List<Revenue> revenues = new ArrayList<>();
        revenues.add(testRevenue);

        when(revenueRepository.findRevenuesByMonth(userId, 2026, 3))
                .thenReturn(revenues);

        List<Revenue> result = revenueService.getRevenuesByMonth(userId, 2026, 3);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(revenueRepository, times(1)).findRevenuesByMonth(userId, 2026, 3);
    }

    @Test
    void testGetCurrentMonthRevenues() {
        List<Revenue> revenues = new ArrayList<>();
        revenues.add(testRevenue);

        when(revenueRepository.findRevenuesByMonth(userId, YearMonth.now().getYear(), YearMonth.now().getMonthValue()))
                .thenReturn(revenues);

        List<Revenue> result = revenueService.getCurrentMonthRevenues(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetTotalRevenue() {
        List<Revenue> revenues = new ArrayList<>();
        revenues.add(testRevenue);

        when(revenueRepository.findRevenuesByMonth(userId, 2026, 3))
                .thenReturn(revenues);

        BigDecimal total = revenueService.getTotalRevenue(userId, 2026, 3);

        assertEquals(BigDecimal.valueOf(5000.00), total);
    }

    @Test
    void testDeleteRevenueSuccess() throws Exception {
        Optional<Revenue> optionalRevenue = Optional.of(testRevenue);
        when(revenueRepository.findById(testRevenue.getRevenueId())).thenReturn(optionalRevenue);

        revenueService.deleteRevenue(userId, testRevenue.getRevenueId());

        verify(revenueRepository, times(1)).delete(testRevenue);
    }

    @Test
    void testDeleteRevenueNotFound() {
        when(revenueRepository.findById("invalidId")).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            revenueService.deleteRevenue(userId, "invalidId");
        });

        assertEquals("Revenue not found", exception.getMessage());
    }
}
