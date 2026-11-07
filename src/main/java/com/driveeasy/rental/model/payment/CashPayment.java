package com.driveeasy.rental.model.payment;

import java.time.LocalDateTime;

/**
 * Pay in cash at the pickup counter. No fee.
 * Stays PENDING until staff mark it as received.
 */
public class CashPayment extends Payment {

    public static final String METHOD = "CASH";

    public CashPayment(String id, String rentalId, String customerId, double amount,
                       LocalDateTime createdAt, PaymentStatus status) {
        super(id, rentalId, customerId, amount, createdAt, status);
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public String getMethodDisplayName() {
        return "Cash at Pickup";
    }

    @Override
    public double getProcessingFee() {
        return 0;
    }

    @Override
    public String getDetails() {
        return "Pay at the counter when collecting the vehicle";
    }

    @Override
    public void process() {
        setStatus(PaymentStatus.PENDING);
    }
}
