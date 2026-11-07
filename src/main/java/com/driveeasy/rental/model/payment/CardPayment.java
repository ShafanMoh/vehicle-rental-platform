package com.driveeasy.rental.model.payment;

import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDateTime;

/**
 * Payment by credit or debit card. Paid immediately, with a 2% card fee.
 * Information hiding: only the last 4 digits of the card are ever stored.
 */
public class CardPayment extends Payment {

    public static final String METHOD = "CARD";
    private static final double CARD_FEE_RATE = 0.02;

    private String cardHolderName;
    private String cardLastFour;

    public CardPayment(String id, String rentalId, String customerId, double amount, LocalDateTime createdAt,
                       PaymentStatus status, String cardHolderName, String cardLastFour) {
        super(id, rentalId, customerId, amount, createdAt, status);
        this.cardHolderName = cardHolderName;
        this.cardLastFour = cardLastFour;
    }

    @Override
    public String getMethod() {
        return METHOD;
    }

    @Override
    public String getMethodDisplayName() {
        return "Credit / Debit Card";
    }

    @Override
    public double getProcessingFee() {
        return getAmount() * CARD_FEE_RATE;
    }

    @Override
    public String getDetails() {
        return cardHolderName + " - card ending " + cardLastFour;
    }

    @Override
    public void process() {
        // A real system would contact a payment gateway here.
        setStatus(PaymentStatus.PAID);
    }

    /** Format: [Payment fields]|cardHolderName|cardLastFour */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(cardHolderName, cardLastFour);
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public String getCardLastFour() {
        return cardLastFour;
    }
}
