package com.dollartracker.dto;

import java.math.BigDecimal;

public class RevenueDto {
    private String revenueId;
    private String source;
    private BigDecimal amount;
    private String transactionDate; // Input: MM/YY or calendar date
    private Boolean isRecurring;
    private String recurringFrequency;
    private String notes;

    public RevenueDto() {}

    public RevenueDto(String revenueId, String source, BigDecimal amount, String transactionDate, 
                      Boolean isRecurring, String recurringFrequency, String notes) {
        this.revenueId = revenueId;
        this.source = source;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.isRecurring = isRecurring;
        this.recurringFrequency = recurringFrequency;
        this.notes = notes;
    }

    public String getRevenueId() { return revenueId; }
    public void setRevenueId(String revenueId) { this.revenueId = revenueId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public String getRecurringFrequency() { return recurringFrequency; }
    public void setRecurringFrequency(String recurringFrequency) { this.recurringFrequency = recurringFrequency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
