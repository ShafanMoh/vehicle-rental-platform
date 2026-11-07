package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.review.GeneralReview;
import com.driveeasy.rental.model.review.Review;
import com.driveeasy.rental.model.review.VerifiedReview;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Stores vehicle reviews in reviews.txt.
 */
@Repository
public class ReviewRepository extends FileRepository<Review> {

    public ReviewRepository(@Value("${app.data-dir}") String dataDirectory) {
        super(dataDirectory, "reviews.txt", "RV");
    }

    @Override
    protected Review parse(String[] f) {
        String type = f[0];
        String id = f[1];
        String vehicleId = f[2];
        String customerId = f[3];
        String customerName = f[4];
        int rating = toInt(f[5]);
        String comment = f[6];
        LocalDate createdOn = toDate(f[7]);

        return switch (type) {
            case VerifiedReview.TYPE -> new VerifiedReview(id, vehicleId, customerId, customerName, rating,
                    comment, createdOn, field(f, 8));
            case GeneralReview.TYPE -> new GeneralReview(id, vehicleId, customerId, customerName, rating,
                    comment, createdOn);
            default -> throw new IllegalArgumentException("Unknown review type " + type);
        };
    }

    public List<Review> findByVehicleId(String vehicleId) {
        return findAll().stream()
                .filter(review -> review.getVehicleId().equals(vehicleId))
                .toList();
    }

    public List<Review> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(review -> review.getCustomerId().equals(customerId))
                .toList();
    }
}
