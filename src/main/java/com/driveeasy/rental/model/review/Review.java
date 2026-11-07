package com.driveeasy.rental.model.review;

import com.driveeasy.rental.model.FileStorable;
import com.driveeasy.rental.model.Identifiable;
import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDate;

/**
 * Base class for a customer review of a vehicle.
 *
 * Polymorphism: a VerifiedReview (written after a completed rental) counts
 * fully towards the vehicle's average rating, a GeneralReview counts half.
 */
public abstract class Review implements Identifiable, FileStorable {

    public static final int MIN_RATING = 1;
    public static final int MAX_RATING = 5;

    private String id;
    private String vehicleId;
    private String customerId;
    private String customerName;
    private int rating;
    private String comment;
    private LocalDate createdOn;

    protected Review(String id, String vehicleId, String customerId, String customerName,
                     int rating, String comment, LocalDate createdOn) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.rating = rating;
        this.comment = comment;
        this.createdOn = createdOn;
    }

    /** Code saved in the file: VERIFIED or GENERAL. */
    public abstract String getReviewType();

    /** Label shown next to the review. */
    public abstract String getBadgeLabel();

    /** How much this review counts towards the average rating. */
    public abstract double getRatingWeight();

    public boolean isWrittenBy(String userId) {
        return customerId.equals(userId);
    }

    /** Format: type|id|vehicleId|customerId|customerName|rating|comment|createdOn */
    @Override
    public String toFileString() {
        return FileUtil.join(getReviewType(), id, vehicleId, customerId, customerName, rating, comment, createdOn);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDate getCreatedOn() {
        return createdOn;
    }
}
