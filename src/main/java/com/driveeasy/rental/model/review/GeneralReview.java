package com.driveeasy.rental.model.review;

import java.time.LocalDate;

/**
 * A review from a customer who has not completed a rental of this vehicle yet.
 */
public class GeneralReview extends Review {

    public static final String TYPE = "GENERAL";

    public GeneralReview(String id, String vehicleId, String customerId, String customerName,
                         int rating, String comment, LocalDate createdOn) {
        super(id, vehicleId, customerId, customerName, rating, comment, createdOn);
    }

    @Override
    public String getReviewType() {
        return TYPE;
    }

    @Override
    public String getBadgeLabel() {
        return "Customer";
    }

    @Override
    public double getRatingWeight() {
        return 0.5;
    }
}
