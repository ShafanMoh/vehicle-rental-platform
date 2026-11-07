package com.driveeasy.rental.service;

import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.exception.ResourceNotFoundException;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.review.GeneralReview;
import com.driveeasy.rental.model.review.Review;
import com.driveeasy.rental.model.review.VerifiedReview;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.repository.ReviewRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Component 5 - Review & Feedback Management.
 * Create: write a review. Read: view reviews and ratings. Update: edit own review.
 * Delete: remove own review, or any review as admin.
 */
@Service
public class ReviewService {

    private static final int MAX_COMMENT_LENGTH = 500;

    private final ReviewRepository reviewRepository;
    private final RentalService rentalService;
    private final UserService userService;
    private final VehicleService vehicleService;

    public ReviewService(ReviewRepository reviewRepository, RentalService rentalService,
                         UserService userService, VehicleService vehicleService) {
        this.reviewRepository = reviewRepository;
        this.rentalService = rentalService;
        this.userService = userService;
        this.vehicleService = vehicleService;
    }

    // ---------- Create ----------

    /** Creates a VerifiedReview if the customer has completed a rental of this vehicle, otherwise a GeneralReview. */
    public Review add(String customerId, String vehicleId, Integer rating, String comment) {
        Customer customer = userService.getCustomer(customerId);
        vehicleService.getById(vehicleId);
        if (findByCustomerAndVehicle(customerId, vehicleId).isPresent()) {
            throw new BusinessException("You have already reviewed this vehicle. You can edit your review instead.");
        }
        int stars = validateRating(rating);
        String text = validateComment(comment);
        LocalDate today = LocalDate.now();

        Optional<Rental> completed = rentalService.findCompletedRental(customerId, vehicleId);
        Review review = completed.isPresent()
                ? new VerifiedReview(null, vehicleId, customerId, customer.getFullName(), stars, text, today,
                        completed.get().getId())
                : new GeneralReview(null, vehicleId, customerId, customer.getFullName(), stars, text, today);
        return reviewRepository.save(review);
    }

    // ---------- Read ----------

    public Review getById(String id) {
        return reviewRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Review", id));
    }

    public List<Review> getForVehicle(String vehicleId) {
        return reviewRepository.findByVehicleId(vehicleId).stream()
                .sorted(Comparator.comparing(Review::getCreatedOn).reversed())
                .toList();
    }

    public List<Review> getByCustomer(String customerId) {
        return reviewRepository.findByCustomerId(customerId);
    }

    public List<Review> search(Integer rating, String type) {
        return reviewRepository.findAll().stream()
                .filter(r -> rating == null || r.getRating() == rating)
                .filter(r -> type == null || type.isBlank() || r.getReviewType().equals(type))
                .sorted(Comparator.comparing(Review::getCreatedOn).reversed())
                .toList();
    }

    public Optional<Review> findByCustomerAndVehicle(String customerId, String vehicleId) {
        return reviewRepository.findByVehicleId(vehicleId).stream()
                .filter(r -> r.isWrittenBy(customerId))
                .findFirst();
    }

    /** Weighted average: each review counts by its own getRatingWeight() (polymorphism). */
    public double getAverageRating(String vehicleId) {
        return weightedAverage(reviewRepository.findByVehicleId(vehicleId));
    }

    /** Average rating of every vehicle, keyed by vehicle ID. */
    public Map<String, Double> getAverageRatingMap() {
        Map<String, List<Review>> byVehicle = reviewRepository.findAll().stream()
                .collect(Collectors.groupingBy(Review::getVehicleId));
        Map<String, Double> averages = new HashMap<>();
        byVehicle.forEach((vehicleId, reviews) -> averages.put(vehicleId, weightedAverage(reviews)));
        return averages;
    }

    public long count() {
        return reviewRepository.findAll().size();
    }

    private double weightedAverage(List<Review> reviews) {
        double totalWeight = 0;
        double weightedSum = 0;
        for (Review review : reviews) {
            totalWeight += review.getRatingWeight();
            weightedSum += review.getRating() * review.getRatingWeight();
        }
        return totalWeight == 0 ? 0 : Math.round(weightedSum / totalWeight * 10) / 10.0;
    }

    // ---------- Update ----------

    public Review update(String reviewId, String userId, Integer rating, String comment) {
        Review review = getById(reviewId);
        if (!review.isWrittenBy(userId)) {
            throw new BusinessException("You can only edit your own reviews.");
        }
        review.setRating(validateRating(rating));
        review.setComment(validateComment(comment));
        reviewRepository.update(review);
        return review;
    }

    // ---------- Delete ----------

    public void delete(String reviewId, String userId) {
        Review review = getById(reviewId);
        User user = userService.getById(userId);
        if (!review.isWrittenBy(userId) && !user.isAdmin()) {
            throw new BusinessException("You can only delete your own reviews.");
        }
        reviewRepository.deleteById(reviewId);
    }

    // ---------- Helpers ----------

    private int validateRating(Integer rating) {
        if (rating == null || rating < Review.MIN_RATING || rating > Review.MAX_RATING) {
            throw new BusinessException("Please choose a rating from 1 to 5 stars.");
        }
        return rating;
    }

    private String validateComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new BusinessException("Please write a short comment.");
        }
        String text = comment.trim();
        if (text.length() > MAX_COMMENT_LENGTH) {
            throw new BusinessException("Comments can be at most " + MAX_COMMENT_LENGTH + " characters.");
        }
        return text;
    }
}
