package com.driveeasy.rental.model.user;

import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDate;

/**
 * A premium member. Multi-level inheritance: PremiumCustomer -> Customer -> User.
 * Polymorphism: overrides the discount and rental limit methods of Customer.
 */
public class PremiumCustomer extends Customer {

    public static final String ROLE = "PREMIUM";
    private static final double PREMIUM_DISCOUNT = 0.10;
    private static final int POINTS_PER_1000_LKR = 1;

    private int loyaltyPoints;

    public PremiumCustomer(String id, String username, String passwordHash, String fullName, String email,
                           String phone, LocalDate registeredDate, String drivingLicenseNumber,
                           String address, int loyaltyPoints) {
        super(id, username, passwordHash, fullName, email, phone, registeredDate, drivingLicenseNumber, address);
        this.loyaltyPoints = loyaltyPoints;
    }

    /** Creates a premium member from an existing regular customer. */
    public PremiumCustomer(Customer customer) {
        super(customer);
        this.loyaltyPoints = 0;
    }

    @Override
    public String getRole() {
        return ROLE;
    }

    @Override
    public String getRoleDisplayName() {
        return "Premium Member";
    }

    @Override
    public double getDiscountRate() {
        return PREMIUM_DISCOUNT;
    }

    @Override
    public int getMaxActiveRentals() {
        return 5;
    }

    @Override
    public boolean isPremium() {
        return true;
    }

    /** Premium members earn 1 point for every LKR 1,000 spent. */
    public void earnPointsFor(double amountSpent) {
        loyaltyPoints += (int) (amountSpent / 1000) * POINTS_PER_1000_LKR;
    }

    /** Format: [Customer fields]|loyaltyPoints */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + loyaltyPoints;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
}
