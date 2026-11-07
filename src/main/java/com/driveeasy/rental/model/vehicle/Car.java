package com.driveeasy.rental.model.vehicle;

import com.driveeasy.rental.util.FileUtil;

/**
 * A standard passenger car. Adds the transmission type.
 */
public class Car extends Vehicle {

    public static final String TYPE = "CAR";
    private static final double INSURANCE_PER_DAY = 750;

    private String transmission;

    public Car(String id, String brand, String model, int year, String plateNumber, double dailyRate,
               int seats, String fuelType, VehicleStatus status, String imageUrl, String description,
               String location, String transmission) {
        super(id, brand, model, year, plateNumber, dailyRate, seats, fuelType, status, imageUrl,
                description, location);
        this.transmission = transmission;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getTypeDisplayName() {
        return "Car";
    }

    @Override
    public double getInsuranceFeePerDay() {
        return INSURANCE_PER_DAY;
    }

    @Override
    public String getSpecSummary() {
        return transmission + " transmission";
    }

    @Override
    public String getIcon() {
        return "bi-car-front-fill";
    }

    /** Format: [Vehicle fields]|transmission */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(transmission);
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }
}
