package com.driveeasy.rental.config;

import com.driveeasy.rental.model.payment.BankTransferPayment;
import com.driveeasy.rental.model.payment.CardPayment;
import com.driveeasy.rental.model.payment.CashPayment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.rental.RentalStatus;
import com.driveeasy.rental.model.review.GeneralReview;
import com.driveeasy.rental.model.review.VerifiedReview;
import com.driveeasy.rental.model.user.Admin;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.PremiumCustomer;
import com.driveeasy.rental.model.vehicle.Car;
import com.driveeasy.rental.model.vehicle.Motorbike;
import com.driveeasy.rental.model.vehicle.SUV;
import com.driveeasy.rental.model.vehicle.Van;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import com.driveeasy.rental.repository.PaymentRepository;
import com.driveeasy.rental.repository.RentalRepository;
import com.driveeasy.rental.repository.ReviewRepository;
import com.driveeasy.rental.repository.UserRepository;
import com.driveeasy.rental.repository.VehicleRepository;
import com.driveeasy.rental.util.PasswordUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Fills the data files with sample records the first time the app runs
 * (only when users.txt and vehicles.txt are empty). Delete the files in /data to start again.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;

    public DataInitializer(UserRepository userRepository, VehicleRepository vehicleRepository,
                           RentalRepository rentalRepository, PaymentRepository paymentRepository,
                           ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.isEmpty() || !vehicleRepository.isEmpty()) {
            return;
        }
        LOG.info("Data files are empty - adding sample data");
        seedUsers();
        seedVehicles();
        seedBookings();
    }

    private void seedUsers() {
        LocalDate today = LocalDate.now();
        userRepository.save(new Admin(null, "admin", PasswordUtil.hash("admin123"), "System Administrator",
                "admin@driveeasy.lk", "0112345678", today.minusMonths(8), "Fleet Manager"));
        userRepository.save(new Customer(null, "nimal", PasswordUtil.hash("nimal123"), "Nimal Perera",
                "nimal@gmail.com", "0771234567", today.minusMonths(5), "B1234567", "12 Galle Road, Colombo 03"));
        userRepository.save(new PremiumCustomer(null, "kasuni", PasswordUtil.hash("kasuni123"), "Kasuni Fernando",
                "kasuni@gmail.com", "0719876543", today.minusMonths(7), "B7654321", "45 Kandy Road, Kadawatha", 120));
        userRepository.save(new Customer(null, "tharindu", PasswordUtil.hash("tharindu123"), "Tharindu Silva",
                "tharindu@yahoo.com", "0765554433", today.minusMonths(2), "B5558888", "8 Lake Drive, Kandy"));
        userRepository.save(new Admin(null, "staff", PasswordUtil.hash("staff123"), "Ishara Jayasinghe",
                "ishara@driveeasy.lk", "0112345679", today.minusMonths(4), "Front Desk Officer"));
    }

    private void seedVehicles() {
        VehicleStatus ok = VehicleStatus.AVAILABLE;
        vehicleRepository.save(new Car(null, "Toyota", "Axio", 2019, "CAB-4521", 9500, 5, "Petrol", ok, "",
                "Comfortable and fuel efficient sedan, ideal for city trips and family travel.", "Colombo", "Automatic"));
        vehicleRepository.save(new Car(null, "Suzuki", "Wagon R", 2021, "CBH-1123", 6500, 4, "Hybrid", ok, "",
                "Compact hybrid hatchback that is easy to park and very economical.", "Kandy", "Automatic"));
        vehicleRepository.save(new Car(null, "Honda", "Civic", 2022, "CBK-7788", 13500, 5, "Petrol", ok, "",
                "Sporty sedan with a modern interior, Apple CarPlay and reverse camera.", "Colombo", "Automatic"));
        vehicleRepository.save(new SUV(null, "Toyota", "Land Cruiser Prado", 2020, "CAR-9090", 32000, 7, "Diesel",
                VehicleStatus.RENTED, "", "Powerful 4x4 for hill country and long family tours.", "Colombo",
                "Automatic", true));
        vehicleRepository.save(new SUV(null, "Mitsubishi", "Outlander", 2021, "CBA-3344", 18500, 7, "Petrol", ok, "",
                "Spacious seven seater SUV with plenty of luggage space.", "Galle", "Automatic", false));
        vehicleRepository.save(new Van(null, "Toyota", "KDH HiAce", 2018, "PH-5566", 16000, 14, "Diesel", ok, "",
                "High roof van with A/C, perfect for group tours and airport transfers.", "Colombo", 1200));
        vehicleRepository.save(new Van(null, "Nissan", "Caravan", 2019, "PG-2211", 14000, 10, "Diesel",
                VehicleStatus.MAINTENANCE, "", "Reliable van for small groups and moving goods.", "Negombo", 1000));
        vehicleRepository.save(new Motorbike(null, "Honda", "Dio", 2022, "BKA-1212", 1800, 2, "Petrol", ok, "",
                "Light scooter for getting around town. Great for tourists.", "Ella", 110, true));
        vehicleRepository.save(new Motorbike(null, "Bajaj", "Pulsar 150", 2021, "BIF-3434", 2500, 2, "Petrol", ok, "",
                "Sporty commuter bike with good mileage.", "Kandy", 150, true));
        vehicleRepository.save(new Car(null, "Toyota", "Prius", 2020, "CAW-6767", 12000, 5, "Hybrid", ok, "",
                "Quiet, efficient hybrid for long drives down the southern coast.", "Galle", "Automatic"));
    }

    /** A few bookings in different states so every page has something to show. */
    private void seedBookings() {
        LocalDate today = LocalDate.now();

        // R001 - Nimal, Toyota Axio, finished last month, paid by card
        Rental r1 = rentalRepository.save(new Rental(null, "U002", "V001", today.minusDays(40), today.minusDays(37),
                "Colombo", RentalStatus.COMPLETED, 30750, 0, 30750, today.minusDays(45)));
        paymentRepository.save(new CardPayment(null, r1.getId(), "U002", 30750,
                today.minusDays(45).atTime(10, 15), PaymentStatus.PAID, "Nimal Perera", "4242"));

        // R002 - Kasuni (premium), Land Cruiser, on rent now, paid by card
        Rental r2 = rentalRepository.save(new Rental(null, "U003", "V004", today.minusDays(2), today.plusDays(3),
                "Colombo", RentalStatus.ONGOING, 166000, 16600, 149400, today.minusDays(6)));
        paymentRepository.save(new CardPayment(null, r2.getId(), "U003", 149400,
                today.minusDays(6).atTime(14, 40), PaymentStatus.PAID, "Kasuni Fernando", "1881"));

        // R003 - Nimal, HiAce van, booked for next week, paying cash at pickup
        Rental r3 = rentalRepository.save(new Rental(null, "U002", "V006", today.plusDays(5), today.plusDays(8),
                "Colombo", RentalStatus.CONFIRMED, 53500, 0, 53500, today.minusDays(1)));
        paymentRepository.save(new CashPayment(null, r3.getId(), "U002", 53500,
                today.minusDays(1).atTime(9, 5), PaymentStatus.PENDING));

        // R004 - Kasuni, Wagon R, finished two months ago, paid by bank transfer
        Rental r4 = rentalRepository.save(new Rental(null, "U003", "V002", today.minusDays(70), today.minusDays(66),
                "Kandy", RentalStatus.COMPLETED, 29000, 2900, 26100, today.minusDays(75)));
        paymentRepository.save(new BankTransferPayment(null, r4.getId(), "U003", 26100,
                today.minusDays(75).atTime(16, 20), PaymentStatus.PAID, "Commercial Bank", "CMB88213"));

        // R005 - Tharindu, Honda Dio, waiting for payment
        rentalRepository.save(new Rental(null, "U004", "V008", today.plusDays(10), today.plusDays(13),
                "Ella", RentalStatus.PENDING_PAYMENT, 6150, 0, 6150, today));

        reviewRepository.save(new VerifiedReview(null, "V001", "U002", "Nimal Perera", 5,
                "Very clean car and smooth pickup. Will rent again!", today.minusDays(36), r1.getId()));
        reviewRepository.save(new VerifiedReview(null, "V002", "U003", "Kasuni Fernando", 4,
                "Great on fuel. A little small for four adults with luggage.", today.minusDays(65), r4.getId()));
        reviewRepository.save(new GeneralReview(null, "V001", "U004", "Tharindu Silva", 4,
                "Friendly staff at the Colombo branch, car looked well maintained.", today.minusDays(20)));
    }
}
