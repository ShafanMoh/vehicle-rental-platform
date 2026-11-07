package com.driveeasy.rental.model.payment;

import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDateTime;

/**
 * Payment by bank transfer. Flat LKR 100 fee.
 * Stays PENDING until an admin checks the transfer reference.
 */
public class BankTransferPayment extends Payment {

    public static final String METHOD = "BANK";
    private static final double TRANSFER_FEE = 100;

    private String bankName;
    private String referenceNumber;

    public BankTransferPayment(String id, String rentalId, String customerId, double amount,
                               LocalDateTime createdAt, PaymentStatus status, String bankName,
                               String referenceNumber) {
        super(id, rentalId, customerId, amount, createdAt, status);
        this.bankName = bankName;
        this.referenceNumber = referenceNumber;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public String getMethodDisplayName() {
        return "Bank Transfer";
    }

    @Override
    public double getProcessingFee() {
        return TRANSFER_FEE;
    }

    @Override
    public String getDetails() {
        return bankName + " - ref " + referenceNumber;
    }

    @Override
    public void process() {
        setStatus(PaymentStatus.PENDING);
    }

    /** Format: [Payment fields]|bankName|referenceNumber */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(bankName, referenceNumber);
    }

    public String getBankName() {
        return bankName;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }
}
