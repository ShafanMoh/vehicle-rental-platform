package com.driveeasy.rental.model.payment;

import com.driveeasy.rental.model.FileStorable;
import com.driveeasy.rental.model.Identifiable;
import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base class for a payment made for a rental.
 *
 * OOP concepts:
 *  - Abstraction: process(), getProcessingFee() and getDetails() are abstract.
 *  - Polymorphism: CardPayment, BankTransferPayment and CashPayment each
 *    process the payment and calculate fees in their own way.
 */
public abstract class Payment implements Identifiable, FileStorable {

    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a");

    private String id;
    private String rentalId;
    private String customerId;
    private double amount;
    private LocalDateTime createdAt;
    private PaymentStatus status;

    protected Payment(String id, String rentalId, String customerId, double amount,
                      LocalDateTime createdAt, PaymentStatus status) {
        this.id = id;
        this.rentalId = rentalId;
        this.customerId = customerId;
        this.amount = amount;
        this.createdAt = createdAt;
        this.status = status;
    }

    // ---------- Abstract methods ----------

    /** Code saved in the file, e.g. CARD. */
    public abstract String getMethod();

    public abstract String getMethodDisplayName();

    /** Extra fee charged by this payment method. */
    public abstract double getProcessingFee();

    /** Method-specific details shown on the receipt. */
    public abstract String getDetails();

    /** Runs the payment and sets its first status (paid now, or pending until verified). */
    public abstract void process();

    // ---------- Shared behaviour ----------

    public double getTotalCharged() {
        return amount + getProcessingFee();
    }

    public void markPaid() {
        this.status = PaymentStatus.PAID;
    }

    public void refund() {
        this.status = PaymentStatus.REFUNDED;
    }

    protected void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getCreatedAtDisplay() {
        return createdAt.format(DISPLAY_FORMAT);
    }

    /** Format: method|id|rentalId|customerId|amount|createdAt|status */
    @Override
    public String toFileString() {
        return FileUtil.join(getMethod(), id, rentalId, customerId, amount, createdAt, status);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getRentalId() {
        return rentalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public PaymentStatus getStatus() {
        return status;
    }
}
