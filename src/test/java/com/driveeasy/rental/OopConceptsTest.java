package com.driveeasy.rental;

import com.driveeasy.rental.model.payment.BankTransferPayment;
import com.driveeasy.rental.model.payment.CardPayment;
import com.driveeasy.rental.model.payment.CashPayment;
import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.PremiumCustomer;
import com.driveeasy.rental.model.vehicle.Car;
import com.driveeasy.rental.model.vehicle.Motorbike;
import com.driveeasy.rental.model.vehicle.Van;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import com.driveeasy.rental.util.FileUtil;
import com.driveeasy.rental.util.PasswordUtil;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Small tests that show the OOP behaviour works as described in the report.
 */
class OopConceptsTest {

    private static final VehicleStatus OK = VehicleStatus.AVAILABLE;

    @Test
    void eachVehicleTypeCalculatesItsOwnPrice() {
        Vehicle car = new Car("V1", "Toyota", "Axio", 2020, "A-1", 10000, 5, "Petrol", OK, "", "", "Colombo", "Automatic");
        Vehicle van = new Van("V2", "Toyota", "HiAce", 2020, "A-2", 10000, 12, "Diesel", OK, "", "", "Colombo", 1000);
        Vehicle bike = new Motorbike("V3", "Honda", "Dio", 2020, "A-3", 2000, 2, "Petrol", OK, "", "", "Ella", 110, true);

        assertEquals((10000 + 750) * 3, car.calculateRentalCost(3));          // insurance only
        assertEquals((10000 + 1000) * 3 + 2500, van.calculateRentalCost(3));  // + cleaning fee
        assertEquals((2000 + 250) * 7 * 0.85, bike.calculateRentalCost(7), 0.001); // weekly discount
    }

    @Test
    void premiumCustomerOverridesDiscountAndLimit() {
        Customer regular = new Customer("U1", "a", "x:y", "A", "a@a.com", "0771234567", LocalDate.now(), "L", "Addr");
        Customer premium = new PremiumCustomer(regular);

        assertEquals(0.0, regular.getDiscountRate());
        assertEquals(0.10, premium.getDiscountRate());
        assertEquals(2, regular.getMaxActiveRentals());
        assertEquals(5, premium.getMaxActiveRentals());
        assertEquals("PREMIUM", premium.getRole());
    }

    @Test
    void paymentMethodsProcessDifferently() {
        LocalDateTime now = LocalDateTime.now();
        List<Payment> payments = List.of(
                new CardPayment("P1", "R1", "U1", 10000, now, PaymentStatus.PENDING, "A", "4242"),
                new BankTransferPayment("P2", "R1", "U1", 10000, now, PaymentStatus.PENDING, "BOC", "REF1"),
                new CashPayment("P3", "R1", "U1", 10000, now, PaymentStatus.PENDING));
        payments.forEach(Payment::process);

        assertEquals(PaymentStatus.PAID, payments.get(0).getStatus());
        assertEquals(PaymentStatus.PENDING, payments.get(1).getStatus());
        assertEquals(200, payments.get(0).getProcessingFee(), 0.001);
        assertEquals(100, payments.get(1).getProcessingFee(), 0.001);
        assertEquals(0, payments.get(2).getProcessingFee(), 0.001);
    }

    @Test
    void fileLinesSurviveSpecialCharacters() {
        String line = FileUtil.join("a|b", "line1\nline2", "back\\slash", "");
        assertArrayEquals(new String[]{"a|b", "line1\nline2", "back\\slash", ""}, FileUtil.split(line));
    }

    @Test
    void passwordsAreHashedNotStored() {
        String hash = PasswordUtil.hash("secret123");
        assertFalse(hash.contains("secret123"));
        assertTrue(PasswordUtil.matches("secret123", hash));
        assertFalse(PasswordUtil.matches("wrong", hash));
    }
}
