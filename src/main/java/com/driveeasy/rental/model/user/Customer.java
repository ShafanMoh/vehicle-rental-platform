package com.driveeasy.rental.model.user;

import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDate;

/**
 * A regular customer who rents vehicles.
 * Inheritance: Customer IS-A User and adds licence and address details.
 */
public class Customer extends User {

    public static final String ROLE = "CUSTOMER";

    private String drivingLicenseNumber;
    private String address;

    public Customer(String id, String username, String passwordHash, String fullName, String email,
                    String phone, LocalDate registeredDate, String drivingLicenseNumber, String address) {
        super(id, username, passwordHash, fullName, email, phone, registeredDate);
        this.drivingLicenseNumber = drivingLicenseNumber;
        this.address = address;
    }

    /** Copy constructor used when changing membership type. */
    public Customer(Customer other) {
        super(other);
        this.drivingLicenseNumber = other.drivingLicenseNumber;
        this.address = other.address;
    }

    @Override
    public String getRole() {
        return ROLE;
    }

    @Override
    public String getRoleDisplayName() {
        return "Regular Customer";
    }

    @Override
    public boolean isAdmin() {
        return false;
    }

    @Override
    public String getHomePage() {
        return "/vehicles";
    }

    /** Percentage taken off each booking (0.0 = no discount). Overridden by PremiumCustomer. */
    public double getDiscountRate() {
        return 0.0;
    }

    /** How many bookings this customer may have open at the same time. */
    public int getMaxActiveRentals() {
        return 2;
    }

    public boolean isPremium() {
        return false;
    }

    /** Format: [User fields]|drivingLicenseNumber|address */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(drivingLicenseNumber, address);
    }

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
