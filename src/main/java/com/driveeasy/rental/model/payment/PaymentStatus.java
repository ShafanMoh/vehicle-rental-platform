package com.driveeasy.rental.model.payment;

public enum PaymentStatus {
    PENDING("Pending", "warning"),
    PAID("Paid", "success"),
    REFUNDED("Refunded", "secondary");

    private final String displayName;
    private final String badgeColor;

    PaymentStatus(String displayName, String badgeColor) {
        this.displayName = displayName;
        this.badgeColor = badgeColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getBadgeColor() {
        return badgeColor;
    }
}
