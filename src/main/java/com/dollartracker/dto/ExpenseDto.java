package com.dollartracker.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseDto {
    private String expenseId;
    private String category;
    private BigDecimal amount;
    private String comments;
    private String transactionDate;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private Boolean isRecurring;
    private String recurringFrequency;

    public ExpenseDto() {}

    public ExpenseDto(String expenseId, String category, BigDecimal amount, String comments, 
                      String transactionDate, LocalDateTime createdDate, LocalDateTime modifiedDate,
                      Boolean isRecurring, String recurringFrequency) {
        this.expenseId = expenseId;
        this.category = category;
        this.amount = amount;
        this.comments = comments;
        this.transactionDate = transactionDate;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.isRecurring = isRecurring;
        this.recurringFrequency = recurringFrequency;
    }

    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getTransactionDate() { return transactionDate; }
    public void setTransactionDate(String transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public String getRecurringFrequency() { return recurringFrequency; }
    public void setRecurringFrequency(String recurringFrequency) { this.recurringFrequency = recurringFrequency; }
}
