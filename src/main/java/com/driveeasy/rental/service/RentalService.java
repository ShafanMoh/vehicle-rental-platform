package com.driveeasy.rental.service;

import com.driveeasy.rental.dto.RentalQuote;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.exception.ResourceNotFoundException;
import com.driveeasy.rental.model.payment.CashPayment;
import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.rental.RentalStatus;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.PremiumCustomer;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import com.driveeasy.rental.repository.PaymentRepository;
import com.driveeasy.rental.repository.RentalRepository;
import com.driveeasy.rental.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Component 3 - Rental (Booking) Management.
 * Create: book a vehicle. Read: view bookings. Update: change dates / status.
 * Delete: cancel a booking or remove an old record.
 */
@Service
public class RentalService {

    private static final int MAX_RENTAL_DAYS = 30;
    private static final int MAX_DAYS_IN_ADVANCE = 180;

    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final VehicleService vehicleService;
    private final UserService userService;

    public RentalService(RentalRepository rentalRepository, PaymentRepository paymentRepository,
                         VehicleService vehicleService, UserService userService) {
        this.rentalRepository = rentalRepository;
        this.paymentRepository = paymentRepository;
        this.vehicleService = vehicleService;
        this.userService = userService;
    }

    // ---------- Pricing ----------

    /**
     * Works out the price. Polymorphism in action: the vehicle decides its own cost
     * (Van adds cleaning, Motorbike gives weekly discount) and the customer decides
     * its own discount (PremiumCustomer gets 10%).
     */
    public RentalQuote quote(Vehicle vehicle, Customer customer, LocalDate start, LocalDate end) {
        validateDates(start, end);
        long days = ChronoUnit.DAYS.between(start, end);
        double base = round(vehicle.calculateRentalCost(days));
        double discount = customer == null ? 0 : round(base * customer.getDiscountRate());
        return new RentalQuote(days, base, discount, base - discount, vehicle.getPricingNote());
    }

    // ---------- Create ----------

    public Rental book(String customerId, String vehicleId, LocalDate start, LocalDate end, String pickupLocation) {
        Customer customer = userService.getCustomer(customerId);
        Vehicle vehicle = vehicleService.getById(vehicleId);
        String location = ValidationUtil.required(pickupLocation, "Pickup location");

        if (!vehicle.isBookable()) {
            throw new BusinessException("This vehicle is under maintenance and cannot be booked right now.");
        }
        long openBookings = rentalRepository.findByCustomerId(customerId).stream()
                .filter(Rental::isBlockingVehicle).count();
        if (openBookings >= customer.getMaxActiveRentals()) {
            throw new BusinessException("You already have " + openBookings + " open bookings. "
                    + customer.getRoleDisplayName() + " accounts can have up to "
                    + customer.getMaxActiveRentals() + " at a time.");
        }
        RentalQuote quote = quote(vehicle, customer, start, end);
        ensureVehicleFree(vehicleId, start, end, null);

        Rental rental = new Rental(null, customerId, vehicleId, start, end, location,
                RentalStatus.PENDING_PAYMENT, quote.baseCost(), quote.discount(), quote.total(), LocalDate.now());
        return rentalRepository.save(rental);
    }

    // ---------- Read ----------

    public Rental getById(String id) {
        return rentalRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking", id));
    }

    /** Returns the booking only if it belongs to this customer. */
    public Rental getForCustomer(String rentalId, String customerId) {
        Rental rental = getById(rentalId);
        if (!rental.getCustomerId().equals(customerId)) {
            throw new BusinessException("You can only view your own bookings.");
        }
        return rental;
    }

    public List<Rental> getByCustomer(String customerId) {
        return rentalRepository.findByCustomerId(customerId).stream()
                .sorted(Comparator.comparing(Rental::getId).reversed())
                .toList();
    }

    public List<Rental> search(String status, String keyword) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase();
        return rentalRepository.findAll().stream()
                .filter(r -> status == null || status.isBlank() || r.getStatus().name().equals(status))
                .filter(r -> query.isEmpty()
                        || r.getId().toLowerCase().contains(query)
                        || r.getCustomerId().toLowerCase().equals(query)
                        || r.getVehicleId().toLowerCase().equals(query))
                .sorted(Comparator.comparing(Rental::getId).reversed())
                .toList();
    }

    public List<Rental> getRecent(int limit) {
        return rentalRepository.findAll().stream()
                .sorted(Comparator.comparing(Rental::getId).reversed())
                .limit(limit)
                .toList();
    }

    /** Booked date ranges of a vehicle, shown on its page so customers can pick free dates. */
    public List<Rental> getUpcomingForVehicle(String vehicleId) {
        return rentalRepository.findByVehicleId(vehicleId).stream()
                .filter(Rental::isBlockingVehicle)
                .filter(r -> !r.getEndDate().isBefore(LocalDate.now()))
                .sorted(Comparator.comparing(Rental::getStartDate))
                .toList();
    }

    /** A completed rental of this vehicle by this customer, used for verified reviews. */
    public Optional<Rental> findCompletedRental(String customerId, String vehicleId) {
        return rentalRepository.findByCustomerId(customerId).stream()
                .filter(r -> r.getVehicleId().equals(vehicleId))
                .filter(r -> r.getStatus() == RentalStatus.COMPLETED)
                .findFirst();
    }

    public long countByStatus(RentalStatus status) {
        return rentalRepository.findAll().stream().filter(r -> r.getStatus() == status).count();
    }

    // ---------- Update ----------

    /** Customer changes pickup/return dates or location before the trip starts. */
    public Rental changeBooking(String rentalId, String customerId, LocalDate start, LocalDate end,
                                String pickupLocation) {
        Rental rental = getForCustomer(rentalId, customerId);
        if (!rental.isEditable()) {
            throw new BusinessException("This booking can no longer be changed.");
        }
        Vehicle vehicle = vehicleService.getById(rental.getVehicleId());
        Customer customer = userService.getCustomer(customerId);
        RentalQuote quote = quote(vehicle, customer, start, end);
        ensureVehicleFree(rental.getVehicleId(), start, end, rentalId);

        Optional<Payment> payment = paymentRepository.findActiveByRentalId(rentalId);
        if (payment.isPresent() && payment.get().getStatus() == PaymentStatus.PAID
                && quote.total() != rental.getTotalCost()) {
            throw new BusinessException("This booking is already paid, so its price cannot change. "
                    + "Choose dates with the same number of days, or cancel and book again.");
        }
        if (payment.isPresent() && payment.get().getStatus() == PaymentStatus.PENDING) {
            // An unpaid cash/bank payment is removed so the customer pays the new amount.
            paymentRepository.deleteById(payment.get().getId());
            rental.reopenForPayment();
        }
        rental.changeDates(start, end, quote.baseCost(), quote.discount());
        rental.setPickupLocation(ValidationUtil.required(pickupLocation, "Pickup location"));
        rentalRepository.update(rental);
        return rental;
    }

    /** Admin moves a booking through its life cycle. */
    public void updateStatus(String rentalId, RentalStatus newStatus) {
        Rental rental = getById(rentalId);
        switch (newStatus) {
            case ONGOING -> startTrip(rental);
            case COMPLETED -> completeTrip(rental);
            case CANCELLED -> cancel(rental);
            case CONFIRMED -> {
                if (!rental.isAwaitingPayment()) {
                    throw new BusinessException("Only bookings awaiting payment can be confirmed.");
                }
                rental.confirm();
            }
            default -> throw new BusinessException("This status cannot be set by hand.");
        }
        rentalRepository.update(rental);
    }

    private void startTrip(Rental rental) {
        if (rental.getStatus() != RentalStatus.CONFIRMED) {
            throw new BusinessException("Only confirmed bookings can be started.");
        }
        Payment payment = paymentRepository.findActiveByRentalId(rental.getId())
                .orElseThrow(() -> new BusinessException("This booking has no payment yet."));
        if (payment.getStatus() == PaymentStatus.PENDING) {
            if (!(payment instanceof CashPayment)) {
                throw new BusinessException("Verify the bank transfer on the Payments page before handing over the vehicle.");
            }
            payment.markPaid(); // cash is collected at the counter at pickup
            paymentRepository.update(payment);
        }
        rental.startTrip();
        vehicleService.changeStatus(rental.getVehicleId(), VehicleStatus.RENTED);
    }

    private void completeTrip(Rental rental) {
        if (rental.getStatus() != RentalStatus.ONGOING) {
            throw new BusinessException("Only bookings that are on rent can be completed.");
        }
        rental.complete();
        vehicleService.changeStatus(rental.getVehicleId(), VehicleStatus.AVAILABLE);
        if (userService.getById(rental.getCustomerId()) instanceof PremiumCustomer premium) {
            premium.earnPointsFor(rental.getTotalCost());
            userService.save(premium);
        }
    }

    // ---------- Delete / cancel ----------

    /** Customer cancels their own booking. */
    public void cancelByCustomer(String rentalId, String customerId) {
        Rental rental = getForCustomer(rentalId, customerId);
        cancel(rental);
        rentalRepository.update(rental);
    }

    private void cancel(Rental rental) {
        if (!rental.isCancellable()) {
            throw new BusinessException("This booking can no longer be cancelled.");
        }
        rental.cancel();
        paymentRepository.findActiveByRentalId(rental.getId()).ifPresent(payment -> {
            payment.refund();
            paymentRepository.update(payment);
        });
    }

    /** Admin removes a finished (completed or cancelled) booking and its payments from the files. */
    public void deleteRecord(String rentalId) {
        Rental rental = getById(rentalId);
        if (rental.isBlockingVehicle()) {
            throw new BusinessException("Only completed or cancelled bookings can be deleted.");
        }
        paymentRepository.findAll().stream()
                .filter(p -> p.getRentalId().equals(rentalId))
                .forEach(p -> paymentRepository.deleteById(p.getId()));
        rentalRepository.deleteById(rentalId);
    }

    // ---------- Helpers ----------

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new BusinessException("Please choose pickup and return dates.");
        }
        if (start.isBefore(LocalDate.now())) {
            throw new BusinessException("Pickup date cannot be in the past.");
        }
        if (!end.isAfter(start)) {
            throw new BusinessException("Return date must be after the pickup date.");
        }
        if (start.isAfter(LocalDate.now().plusDays(MAX_DAYS_IN_ADVANCE))) {
            throw new BusinessException("Bookings can be made up to " + MAX_DAYS_IN_ADVANCE + " days in advance.");
        }
        if (start.plusDays(MAX_RENTAL_DAYS).isBefore(end)) {
            throw new BusinessException("A single booking can be at most " + MAX_RENTAL_DAYS + " days.");
        }
    }

    private void ensureVehicleFree(String vehicleId, LocalDate start, LocalDate end, String ignoreRentalId) {
        boolean clash = rentalRepository.findByVehicleId(vehicleId).stream()
                .filter(r -> !r.getId().equals(ignoreRentalId))
                .filter(Rental::isBlockingVehicle)
                .anyMatch(r -> r.overlaps(start, end));
        if (clash) {
            throw new BusinessException("Sorry, this vehicle is already booked for some of those dates.");
        }
    }

    private static double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
