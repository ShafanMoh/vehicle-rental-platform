package com.driveeasy.rental.model.vehicle;

import com.driveeasy.rental.util.FileUtil;

/**
 * A sport utility vehicle. Multi-level inheritance: SUV -> Car -> Vehicle.
 * Adds four-wheel drive and charges a higher insurance fee.
 */
public class SUV extends Car {

    public static final String TYPE = "SUV";
    private static final double INSURANCE_PER_DAY = 1200;

    private boolean fourWheelDrive;

    public SUV(String id, String brand, String model, int year, String plateNumber, double dailyRate,
               int seats, String fuelType, VehicleStatus status, String imageUrl, String description,
               String location, String transmission, boolean fourWheelDrive) {
        super(id, brand, model, year, plateNumber, dailyRate, seats, fuelType, status, imageUrl,
                description, location, transmission);
        this.fourWheelDrive = fourWheelDrive;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public String getTypeDisplayName() {
        return "SUV";
    }

    @Override
    public double getInsuranceFeePerDay() {
        return INSURANCE_PER_DAY;
    }

    @Override
    public String getSpecSummary() {
        return super.getSpecSummary() + (fourWheelDrive ? " - 4WD" : " - 2WD");
    }

    @Override
    public String getIcon() {
        return "bi-car-front";
    }

    /** Format: [Car fields]|fourWheelDrive */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + fourWheelDrive;
    }

    public boolean isFourWheelDrive() {
        return fourWheelDrive;
    }

    public void setFourWheelDrive(boolean fourWheelDrive) {
        this.fourWheelDrive = fourWheelDrive;
    }
}
