package com.dollartracker.controller;

import com.dollartracker.dto.ApiResponse;
import com.dollartracker.dto.RevenueDto;
import com.dollartracker.entity.Revenue;
import com.dollartracker.service.RevenueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/revenues")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RevenueController {

    @Autowired
    private RevenueService revenueService;

    @PostMapping
    public ResponseEntity<ApiResponse> createRevenue(@RequestBody RevenueDto revenueDto, Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            Revenue revenue = revenueService.createRevenue(userId, revenueDto);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(revenue)
                    .message("Revenue entry created successfully")
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

    @PutMapping("/{revenueId}")
    public ResponseEntity<ApiResponse> updateRevenue(@PathVariable String revenueId, @RequestBody RevenueDto revenueDto, Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            Revenue revenue = revenueService.updateRevenue(userId, revenueId, revenueDto);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(revenue)
                    .message("Revenue entry updated successfully")
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

    @GetMapping("/current-month")
    public ResponseEntity<ApiResponse> getCurrentMonthRevenues(Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            YearMonth currentMonth = YearMonth.now();
            List<Revenue> revenues = revenueService.getRevenuesByMonth(userId, currentMonth.getYear(), currentMonth.getMonthValue());
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .data(revenues)
                    .message("Current month revenues retrieved successfully")
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

    @DeleteMapping("/{revenueId}")
    public ResponseEntity<ApiResponse> deleteRevenue(@PathVariable String revenueId, Authentication authentication) {
        try {
            String userId = (String) authentication.getPrincipal();
            revenueService.deleteRevenue(userId, revenueId);
            
            ApiResponse apiResponse = ApiResponse.builder()
                    .success(true)
                    .message("Revenue entry deleted successfully")
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
