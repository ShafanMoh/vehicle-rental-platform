package com.driveeasy.rental.model.vehicle;

/**
 * The current state of a vehicle in the fleet.
 */
public enum VehicleStatus {
    AVAILABLE("Available", "success"),
    RENTED("On Rent", "primary"),
    MAINTENANCE("Under Maintenance", "warning");

    private final String displayName;
    private final String badgeColor;

    VehicleStatus(String displayName, String badgeColor) {
        this.displayName = displayName;
        this.badgeColor = badgeColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    /** Bootstrap colour name used for the status badge. */
    public String getBadgeColor() {
        return badgeColor;
    }
}
