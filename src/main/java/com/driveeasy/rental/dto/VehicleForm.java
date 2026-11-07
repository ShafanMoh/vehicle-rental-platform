package com.driveeasy.rental.dto;

import com.driveeasy.rental.model.vehicle.Car;
import com.driveeasy.rental.model.vehicle.Motorbike;
import com.driveeasy.rental.model.vehicle.SUV;
import com.driveeasy.rental.model.vehicle.Van;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.model.vehicle.VehicleStatus;

/**
 * Holds the values typed into the add/edit vehicle form.
 * Type-specific fields are only used for the matching vehicle type.
 */
public class VehicleForm {

    private String type = Car.TYPE;
    private String brand;
    private String model;
    private Integer year;
    private String plateNumber;
    private Double dailyRate;
    private Integer seats;
    private String fuelType;
    private String status = VehicleStatus.AVAILABLE.name();
    private String imageUrl;
    private String description;
    private String location;

    // Car and SUV
    private String transmission = "Automatic";
    // SUV
    private boolean fourWheelDrive;
    // Van
    private Integer cargoCapacityKg;
    // Motorbike
    private Integer engineCc;
    private boolean helmetIncluded;

    /** Fills the form with an existing vehicle's details (for the edit page). */
    public static VehicleForm fromVehicle(Vehicle vehicle) {
        VehicleForm form = new VehicleForm();
        form.type = vehicle.getType();
        form.brand = vehicle.getBrand();
        form.model = vehicle.getModel();
        form.year = vehicle.getYear();
        form.plateNumber = vehicle.getPlateNumber();
        form.dailyRate = vehicle.getDailyRate();
        form.seats = vehicle.getSeats();
        form.fuelType = vehicle.getFuelType();
        form.status = vehicle.getStatus().name();
        form.imageUrl = vehicle.getImageUrl();
        form.description = vehicle.getDescription();
        form.location = vehicle.getLocation();

        if (vehicle instanceof Car car) {
            form.transmission = car.getTransmission();
        }
        if (vehicle instanceof SUV suv) {
            form.fourWheelDrive = suv.isFourWheelDrive();
        }
        if (vehicle instanceof Van van) {
            form.cargoCapacityKg = van.getCargoCapacityKg();
        }
        if (vehicle instanceof Motorbike bike) {
            form.engineCc = bike.getEngineCc();
            form.helmetIncluded = bike.isHelmetIncluded();
        }
        return form;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public Double getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(Double dailyRate) {
        this.dailyRate = dailyRate;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public boolean isFourWheelDrive() {
        return fourWheelDrive;
    }

    public void setFourWheelDrive(boolean fourWheelDrive) {
        this.fourWheelDrive = fourWheelDrive;
    }

    public Integer getCargoCapacityKg() {
        return cargoCapacityKg;
    }

    public void setCargoCapacityKg(Integer cargoCapacityKg) {
        this.cargoCapacityKg = cargoCapacityKg;
    }

    public Integer getEngineCc() {
        return engineCc;
    }

    public void setEngineCc(Integer engineCc) {
        this.engineCc = engineCc;
    }

    public boolean isHelmetIncluded() {
        return helmetIncluded;
    }

    public void setHelmetIncluded(boolean helmetIncluded) {
        this.helmetIncluded = helmetIncluded;
    }
}
