package com.driveeasy.rental.service;

import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.rental.RentalStatus;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Component 6 - Admin Dashboard. Collects figures from the other services.
 */
@Service
public class DashboardService {

    private static final DateTimeFormatter MONTH_LABEL = DateTimeFormatter.ofPattern("MMM yyyy");
    private static final int MONTHS_SHOWN = 6;

    private final VehicleService vehicleService;
    private final RentalService rentalService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final ReviewService reviewService;

    public DashboardService(VehicleService vehicleService, RentalService rentalService,
                            PaymentService paymentService, UserService userService, ReviewService reviewService) {
        this.vehicleService = vehicleService;
        this.rentalService = rentalService;
        this.paymentService = paymentService;
        this.userService = userService;
        this.reviewService = reviewService;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalVehicles", vehicleService.getAll().size());
        stats.put("availableVehicles", vehicleService.countByStatus(VehicleStatus.AVAILABLE));
        stats.put("rentedVehicles", vehicleService.countByStatus(VehicleStatus.RENTED));
        stats.put("maintenanceVehicles", vehicleService.countByStatus(VehicleStatus.MAINTENANCE));
        stats.put("customers", userService.countCustomers());
        stats.put("pendingBookings", rentalService.countByStatus(RentalStatus.PENDING_PAYMENT));
        stats.put("confirmedBookings", rentalService.countByStatus(RentalStatus.CONFIRMED));
        stats.put("ongoingBookings", rentalService.countByStatus(RentalStatus.ONGOING));
        stats.put("completedBookings", rentalService.countByStatus(RentalStatus.COMPLETED));
        stats.put("revenue", paymentService.getTotalRevenue());
        stats.put("pendingPayments", paymentService.countPending());
        stats.put("reviews", reviewService.count());
        return stats;
    }

    /** Revenue for each of the last 6 months, oldest first. */
    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> months = new LinkedHashMap<>();
        YearMonth current = YearMonth.now();
        for (int i = MONTHS_SHOWN - 1; i >= 0; i--) {
            months.put(current.minusMonths(i).format(MONTH_LABEL), 0.0);
        }
        for (Payment payment : paymentService.getAll()) {
            if (payment.getStatus() != PaymentStatus.PAID) {
                continue;
            }
            String label = YearMonth.from(payment.getCreatedAt()).format(MONTH_LABEL);
            months.computeIfPresent(label, (key, total) -> total + payment.getTotalCharged());
        }
        return months;
    }
}
