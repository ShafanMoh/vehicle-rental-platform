package com.driveeasy.rental.model.vehicle;

import com.driveeasy.rental.model.FileStorable;
import com.driveeasy.rental.model.Identifiable;
import com.driveeasy.rental.util.FileUtil;

/**
 * Base class for every vehicle that can be rented.
 *
 * OOP concepts:
 *  - Abstraction: abstract class with abstract methods for type-specific details.
 *  - Encapsulation: private fields with getters/setters.
 *  - Polymorphism: calculateRentalCost() and getInsuranceFeePerDay() behave
 *    differently for Car, SUV, Van and Motorbike.
 */
public abstract class Vehicle implements Identifiable, FileStorable {

    private String id;
    private String brand;
    private String model;
    private int year;
    private String plateNumber;
    private double dailyRate;
    private int seats;
    private String fuelType;
    private VehicleStatus status;
    private String imageUrl;
    private String description;
    private String location;

    protected Vehicle(String id, String brand, String model, int year, String plateNumber, double dailyRate,
                      int seats, String fuelType, VehicleStatus status, String imageUrl,
                      String description, String location) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.plateNumber = plateNumber;
        this.dailyRate = dailyRate;
        this.seats = seats;
        this.fuelType = fuelType;
        this.status = status;
        this.imageUrl = imageUrl;
        this.description = description;
        this.location = location;
    }

    // ---------- Abstract methods ----------

    /** Code saved in the file, e.g. CAR, SUV, VAN, MOTORBIKE. */
    public abstract String getType();

    public abstract String getTypeDisplayName();

    /** Insurance charged per rental day. Different for each vehicle type. */
    public abstract double getInsuranceFeePerDay();

    /** Short line of type-specific features, e.g. "Automatic - 4WD". */
    public abstract String getSpecSummary();

    /** Bootstrap icon name for this vehicle type. */
    public abstract String getIcon();

    // ---------- Shared behaviour ----------

    /**
     * Price for a rental before any customer discount.
     * Subclasses can override this to add their own charges or discounts.
     */
    public double calculateRentalCost(long days) {
        return (dailyRate + getInsuranceFeePerDay()) * days;
    }

    /** Explains extra charges or discounts, shown on the booking page. Empty when none. */
    public String getPricingNote() {
        return "";
    }

    public boolean isBookable() {
        return status != VehicleStatus.MAINTENANCE;
    }

    public String getDisplayName() {
        return brand + " " + model;
    }

    /** Uses the uploaded image link, or a picture for the vehicle type when none was given. */
    public String getDisplayImage() {
        if (imageUrl == null || imageUrl.isBlank()) {
            return "/images/" + getType().toLowerCase() + ".svg";
        }
        return imageUrl;
    }

    /**
     * Writes the common fields. Subclasses add their own after these.
     * Format: type|id|brand|model|year|plate|dailyRate|seats|fuel|status|imageUrl|description|location
     */
    @Override
    public String toFileString() {
        return FileUtil.join(getType(), id, brand, model, year, plateNumber, dailyRate, seats,
                fuelType, status, imageUrl, description, location);
    }

    // ---------- Getters and setters ----------

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
