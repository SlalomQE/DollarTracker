package com.dollartracker.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "revenues", indexes = {
    @Index(name = "idx_revenue_user_id", columnList = "user_id"),
    @Index(name = "idx_revenue_transaction_date", columnList = "transaction_date")
})
public class Revenue {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String revenueId = UUID.randomUUID().toString();

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private RevenueSource source;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

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

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public Revenue() {}

    public Revenue(String revenueId, String userId, RevenueSource source, BigDecimal amount,
                   LocalDateTime transactionDate, LocalDateTime createdDate, LocalDateTime modifiedDate,
                   Boolean isRecurring, RecurringFrequency recurringFrequency, String notes) {
        this.revenueId = revenueId;
        this.userId = userId;
        this.source = source;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.isRecurring = isRecurring;
        this.recurringFrequency = recurringFrequency;
        this.notes = notes;
    }

    @PreUpdate
    protected void onUpdate() {
        modifiedDate = LocalDateTime.now();
    }

    public String getRevenueId() { return revenueId; }
    public void setRevenueId(String revenueId) { this.revenueId = revenueId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public RevenueSource getSource() { return source; }
    public void setSource(RevenueSource source) { this.source = source; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public enum RevenueSource {
        SALARY("Salary"),
        RENTAL_INCOME("Rental Income"),
        FROM_BUSINESS("From Business"),
        OTHER("Other");

        private final String displayName;

        RevenueSource(String displayName) {
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
