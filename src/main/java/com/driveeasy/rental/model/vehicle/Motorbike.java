package com.driveeasy.rental.model.vehicle;

import com.driveeasy.rental.util.FileUtil;

/**
 * A motorbike or scooter. Adds engine size and helmet details.
 * Polymorphism: gives a 15% discount on rentals of 7 days or more.
 */
public class Motorbike extends Vehicle {

    public static final String TYPE = "MOTORBIKE";
    private static final double INSURANCE_PER_DAY = 250;
    private static final int WEEKLY_DAYS = 7;
    private static final double WEEKLY_DISCOUNT = 0.15;

    private int engineCc;
    private boolean helmetIncluded;

    public Motorbike(String id, String brand, String model, int year, String plateNumber, double dailyRate,
                     int seats, String fuelType, VehicleStatus status, String imageUrl, String description,
                     String location, int engineCc, boolean helmetIncluded) {
        super(id, brand, model, year, plateNumber, dailyRate, seats, fuelType, status, imageUrl,
                description, location);
        this.engineCc = engineCc;
        this.helmetIncluded = helmetIncluded;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getTypeDisplayName() {
        return "Motorbike";
    }

    @Override
    public double getInsuranceFeePerDay() {
        return INSURANCE_PER_DAY;
    }

    @Override
    public double calculateRentalCost(long days) {
        double cost = super.calculateRentalCost(days);
        if (days >= WEEKLY_DAYS) {
            cost = cost * (1 - WEEKLY_DISCOUNT);
        }
        return cost;
    }

    @Override
    public String getPricingNote() {
        return "Rent for 7 days or more and get 15% off.";
    }

    @Override
    public String getSpecSummary() {
        return engineCc + " cc" + (helmetIncluded ? " - Helmet included" : "");
    }

    @Override
    public String getIcon() {
        return "bi-scooter";
    }

    /** Format: [Vehicle fields]|engineCc|helmetIncluded */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(engineCc, helmetIncluded);
    }

    public int getEngineCc() {
        return engineCc;
    }

    public void setEngineCc(int engineCc) {
        this.engineCc = engineCc;
    }

    public boolean isHelmetIncluded() {
        return helmetIncluded;
    }

    public void setHelmetIncluded(boolean helmetIncluded) {
        this.helmetIncluded = helmetIncluded;
    }
}
