package com.driveeasy.rental.model.rental;

/**
 * Life cycle of a booking:
 * PENDING_PAYMENT -> CONFIRMED -> ONGOING -> COMPLETED, or CANCELLED at the early stages.
 */
public enum RentalStatus {
    PENDING_PAYMENT("Awaiting Payment", "warning"),
    CONFIRMED("Confirmed", "info"),
    ONGOING("On Rent", "primary"),
    COMPLETED("Completed", "success"),
    CANCELLED("Cancelled", "secondary");

    private final String displayName;
    private final String badgeColor;

    RentalStatus(String displayName, String badgeColor) {
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
