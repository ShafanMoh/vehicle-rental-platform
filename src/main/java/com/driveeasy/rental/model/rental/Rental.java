package com.driveeasy.rental.model.rental;

import com.driveeasy.rental.model.FileStorable;
import com.driveeasy.rental.model.Identifiable;
import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * A booking of one vehicle by one customer for a date range.
 * Encapsulation: the status can only change through the methods below,
 * which check that the change is allowed.
 */
public class Rental implements Identifiable, FileStorable {

    private String id;
    private String customerId;
    private String vehicleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String pickupLocation;
    private RentalStatus status;
    private double baseCost;
    private double discountAmount;
    private double totalCost;
    private LocalDate bookedOn;

    public Rental(String id, String customerId, String vehicleId, LocalDate startDate, LocalDate endDate,
                  String pickupLocation, RentalStatus status, double baseCost, double discountAmount,
                  double totalCost, LocalDate bookedOn) {
        this.id = id;
        this.customerId = customerId;
        this.vehicleId = vehicleId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickupLocation = pickupLocation;
        this.status = status;
        this.baseCost = baseCost;
        this.discountAmount = discountAmount;
        this.totalCost = totalCost;
        this.bookedOn = bookedOn;
    }

    /** Number of rental days (pickup day up to, but not including, return day). */
    public long getDays() {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /** True if this booking uses any day between the given dates. */
    public boolean overlaps(LocalDate otherStart, LocalDate otherEnd) {
        return otherStart.isBefore(endDate) && otherEnd.isAfter(startDate);
    }

    /** True while the booking still holds the vehicle for its dates. */
    public boolean isBlockingVehicle() {
        return status == RentalStatus.PENDING_PAYMENT
                || status == RentalStatus.CONFIRMED
                || status == RentalStatus.ONGOING;
    }

    /** Dates can be changed only before pickup. */
    public boolean isEditable() {
        return (status == RentalStatus.PENDING_PAYMENT || status == RentalStatus.CONFIRMED)
                && startDate.isAfter(LocalDate.now());
    }

    public boolean isCancellable() {
        return status == RentalStatus.PENDING_PAYMENT || status == RentalStatus.CONFIRMED;
    }

    public boolean isAwaitingPayment() {
        return status == RentalStatus.PENDING_PAYMENT;
    }

    public void changeDates(LocalDate newStart, LocalDate newEnd, double newBaseCost, double newDiscount) {
        this.startDate = newStart;
        this.endDate = newEnd;
        updateCost(newBaseCost, newDiscount);
    }

    public void updateCost(double newBaseCost, double newDiscount) {
        this.baseCost = newBaseCost;
        this.discountAmount = newDiscount;
        this.totalCost = newBaseCost - newDiscount;
    }

    /** Sends the booking back to "awaiting payment" after an unpaid payment is removed. */
    public void reopenForPayment() {
        this.status = RentalStatus.PENDING_PAYMENT;
    }

    public void confirm() {
        this.status = RentalStatus.CONFIRMED;
    }

    public void cancel() {
        this.status = RentalStatus.CANCELLED;
    }

    public void startTrip() {
        this.status = RentalStatus.ONGOING;
    }

    public void complete() {
        this.status = RentalStatus.COMPLETED;
    }

    /**
     * Format: id|customerId|vehicleId|startDate|endDate|pickupLocation|status|baseCost|discount|totalCost|bookedOn
     */
    @Override
    public String toFileString() {
        return FileUtil.join(id, customerId, vehicleId, startDate, endDate, pickupLocation, status,
                baseCost, discountAmount, totalCost, bookedOn);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public RentalStatus getStatus() {
        return status;
    }

    public double getBaseCost() {
        return baseCost;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public LocalDate getBookedOn() {
        return bookedOn;
    }
}
