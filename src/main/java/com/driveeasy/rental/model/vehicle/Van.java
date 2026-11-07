package com.driveeasy.rental.model.vehicle;

import com.driveeasy.rental.util.FileUtil;

/**
 * A van for groups or goods. Adds cargo capacity.
 * Polymorphism: adds a one-time cleaning fee to the rental cost.
 */
public class Van extends Vehicle {

    public static final String TYPE = "VAN";
    private static final double INSURANCE_PER_DAY = 1000;
    private static final double CLEANING_FEE = 2500;

    private int cargoCapacityKg;

    public Van(String id, String brand, String model, int year, String plateNumber, double dailyRate,
               int seats, String fuelType, VehicleStatus status, String imageUrl, String description,
               String location, int cargoCapacityKg) {
        super(id, brand, model, year, plateNumber, dailyRate, seats, fuelType, status, imageUrl,
                description, location);
        this.cargoCapacityKg = cargoCapacityKg;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getTypeDisplayName() {
        return "Van";
    }

    @Override
    public double getInsuranceFeePerDay() {
        return INSURANCE_PER_DAY;
    }

    @Override
    public double calculateRentalCost(long days) {
        return super.calculateRentalCost(days) + CLEANING_FEE;
    }

    @Override
    public String getPricingNote() {
        return "Includes a one-time cleaning fee of LKR 2,500.";
    }

    @Override
    public String getSpecSummary() {
        return cargoCapacityKg + " kg cargo capacity";
    }

    @Override
    public String getIcon() {
        return "bi-truck-front-fill";
    }

    /** Format: [Vehicle fields]|cargoCapacityKg */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + cargoCapacityKg;
    }

    public int getCargoCapacityKg() {
        return cargoCapacityKg;
    }

    public void setCargoCapacityKg(int cargoCapacityKg) {
        this.cargoCapacityKg = cargoCapacityKg;
    }
}
