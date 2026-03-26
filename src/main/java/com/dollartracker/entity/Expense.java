package com.dollartracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "expenses", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_transaction_date", columnList = "transaction_date")
})
public class Expense {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String expenseId = UUID.randomUUID().toString();

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ExpenseCategory category;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "is_recurring", nullable = false)
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "recurring_frequency")
    private RecurringFrequency recurringFrequency;

    public Expense() {}

    public Expense(String expenseId, String userId, ExpenseCategory category, BigDecimal amount,
                   String comments, LocalDateTime transactionDate, LocalDateTime createdDate,
                   LocalDateTime modifiedDate, Boolean isRecurring, RecurringFrequency recurringFrequency) {
        this.expenseId = expenseId;
        this.userId = userId;
        this.category = category;
        this.amount = amount;
        this.comments = comments;
        this.transactionDate = transactionDate;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.isRecurring = isRecurring;
        this.recurringFrequency = recurringFrequency;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    public String getExpenseId() { return expenseId; }
    public void setExpenseId(String expenseId) { this.expenseId = expenseId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public ExpenseCategory getCategory() { return category; }
    public void setCategory(ExpenseCategory category) { this.category = category; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public LocalDateTime getTransactionDate() { return transactionDate; }
    public void setTransactionDate(LocalDateTime transactionDate) { this.transactionDate = transactionDate; }

    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }

    public LocalDateTime getModifiedDate() { return modifiedDate; }
    public void setModifiedDate(LocalDateTime modifiedDate) { this.modifiedDate = modifiedDate; }

    public Boolean getIsRecurring() { return isRecurring; }
    public void setIsRecurring(Boolean isRecurring) { this.isRecurring = isRecurring; }

    public RecurringFrequency getRecurringFrequency() { return recurringFrequency; }
    public void setRecurringFrequency(RecurringFrequency recurringFrequency) { this.recurringFrequency = recurringFrequency; }

    public enum ExpenseCategory {
        MORTGAGE_RENT("Mortgage/Rent"),
        GROCERIES("Groceries"),
        CAR_EMI("Car EMI"),
        DAYCARE("Daycare"),
        FAMILY_DINNER("Family Dinner"),
        MISCELLANEOUS("Miscellaneous");

        private final String displayName;

        ExpenseCategory(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum RecurringFrequency {
        DAILY("Daily"),
        WEEKLY("Weekly"),
        MONTHLY("Monthly"),
        YEARLY("Yearly");

        private final String displayName;

        RecurringFrequency(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
