package com.driveeasy.rental.model.review;

import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDate;

/**
 * A review from a customer who has completed a rental of this vehicle.
 */
public class VerifiedReview extends Review {

    public static final String TYPE = "VERIFIED";

    private String rentalId;

    public VerifiedReview(String id, String vehicleId, String customerId, String customerName,
                          int rating, String comment, LocalDate createdOn, String rentalId) {
        super(id, vehicleId, customerId, customerName, rating, comment, createdOn);
        this.rentalId = rentalId;
    }

    @Override
    public String getReviewType() {
        return TYPE;
    }

    @Override
    public String getBadgeLabel() {
        return "Verified Renter";
    }

    @Override
    public double getRatingWeight() {
        return 1.0;
    }

    /** Format: [Review fields]|rentalId */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(rentalId);
    }

    public String getRentalId() {
        return rentalId;
    }
}
