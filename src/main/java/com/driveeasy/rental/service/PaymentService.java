package com.driveeasy.rental.service;

import com.driveeasy.rental.dto.PaymentForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.exception.ResourceNotFoundException;
import com.driveeasy.rental.model.payment.BankTransferPayment;
import com.driveeasy.rental.model.payment.CardPayment;
import com.driveeasy.rental.model.payment.CashPayment;
import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.repository.PaymentRepository;
import com.driveeasy.rental.repository.RentalRepository;
import com.driveeasy.rental.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Component 4 - Payment Management.
 * Create: pay for a booking. Read: payment history and receipts.
 * Update: admin marks payments as received or refunds them.
 */
@Service
public class PaymentService {

    private static final DateTimeFormatter EXPIRY_FORMAT = DateTimeFormatter.ofPattern("MM/yy");

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;

    public PaymentService(PaymentRepository paymentRepository, RentalRepository rentalRepository) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
    }

    // ---------- Create ----------

    public Payment pay(String rentalId, String customerId, PaymentForm form) {
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking", rentalId));
        if (!rental.getCustomerId().equals(customerId)) {
            throw new BusinessException("You can only pay for your own bookings.");
        }
        if (!rental.isAwaitingPayment()) {
            throw new BusinessException("This booking does not need a payment.");
        }

        Payment payment = buildPayment(rental, form);
        payment.process(); // polymorphic: card is paid now, cash/bank wait for staff
        paymentRepository.save(payment);

        rental.confirm();
        rentalRepository.update(rental);
        return payment;
    }

    /** Factory: creates the right Payment subclass for the chosen method. */
    private Payment buildPayment(Rental rental, PaymentForm form) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String method = form.getMethod() == null ? "" : form.getMethod();
        return switch (method) {
            case CardPayment.METHOD -> {
                String holder = ValidationUtil.required(form.getCardHolderName(), "Card holder name");
                String digits = validateCard(form);
                yield new CardPayment(null, rental.getId(), rental.getCustomerId(), rental.getTotalCost(), now,
                        PaymentStatus.PENDING, holder, digits.substring(digits.length() - 4));
            }
            case BankTransferPayment.METHOD -> new BankTransferPayment(null, rental.getId(), rental.getCustomerId(),
                    rental.getTotalCost(), now, PaymentStatus.PENDING,
                    ValidationUtil.required(form.getBankName(), "Bank name"),
                    ValidationUtil.required(form.getReferenceNumber(), "Transfer reference number"));
            case CashPayment.METHOD -> new CashPayment(null, rental.getId(), rental.getCustomerId(),
                    rental.getTotalCost(), now, PaymentStatus.PENDING);
            default -> throw new BusinessException("Please choose a payment method.");
        };
    }

    /** Checks card number (Luhn check), expiry and CVV. Returns the card digits. */
    private String validateCard(PaymentForm form) {
        String digits = form.getCardNumber() == null ? "" : form.getCardNumber().replaceAll("\\D", "");
        if (digits.length() < 13 || digits.length() > 19 || !passesLuhnCheck(digits)) {
            throw new BusinessException("Please enter a valid card number.");
        }
        try {
            YearMonth expiry = YearMonth.parse(ValidationUtil.required(form.getExpiry(), "Expiry date"), EXPIRY_FORMAT);
            if (expiry.isBefore(YearMonth.now())) {
                throw new BusinessException("This card has expired.");
            }
        } catch (DateTimeParseException e) {
            throw new BusinessException("Expiry date must be in MM/YY format.");
        }
        if (form.getCvv() == null || !form.getCvv().matches("\\d{3,4}")) {
            throw new BusinessException("CVV must be 3 or 4 digits.");
        }
        return digits;
    }

    private boolean passesLuhnCheck(String digits) {
        int sum = 0;
        boolean doubleIt = false;
        for (int i = digits.length() - 1; i >= 0; i--) {
            int digit = digits.charAt(i) - '0';
            if (doubleIt) {
                digit *= 2;
                if (digit > 9) {
                    digit -= 9;
                }
            }
            sum += digit;
            doubleIt = !doubleIt;
        }
        return sum % 10 == 0;
    }

    // ---------- Read ----------

    public Payment getById(String id) {
        return paymentRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Payment", id));
    }

    public Payment getForCustomer(String paymentId, String customerId) {
        Payment payment = getById(paymentId);
        if (!payment.getCustomerId().equals(customerId)) {
            throw new BusinessException("You can only view your own payments.");
        }
        return payment;
    }

    public List<Payment> getByCustomer(String customerId) {
        return paymentRepository.findByCustomerId(customerId).stream()
                .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
                .toList();
    }

    public List<Payment> search(String status, String method) {
        return paymentRepository.findAll().stream()
                .filter(p -> status == null || status.isBlank() || p.getStatus().name().equals(status))
                .filter(p -> method == null || method.isBlank() || p.getMethod().equals(method))
                .sorted(Comparator.comparing(Payment::getCreatedAt).reversed())
                .toList();
    }

    public Optional<Payment> findForRental(String rentalId) {
        return paymentRepository.findActiveByRentalId(rentalId);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    /** Total money received (including fees) from paid payments. */
    public double getTotalRevenue() {
        return paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .mapToDouble(Payment::getTotalCharged)
                .sum();
    }

    public long countPending() {
        return paymentRepository.findAll().stream().filter(p -> p.getStatus() == PaymentStatus.PENDING).count();
    }

    // ---------- Update ----------

    /** Admin confirms that a cash or bank payment was received. */
    public void markPaid(String paymentId) {
        Payment payment = getById(paymentId);
        if (payment.getStatus() != PaymentStatus.PENDING) {
            throw new BusinessException("Only pending payments can be marked as paid.");
        }
        payment.markPaid();
        paymentRepository.update(payment);
    }

    /** Admin refunds a payment. The booking is cancelled if it has not started yet. */
    public void refund(String paymentId) {
        Payment payment = getById(paymentId);
        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("This payment was already refunded.");
        }
        Rental rental = rentalRepository.findById(payment.getRentalId()).orElse(null);
        if (rental != null && !rental.isCancellable()) {
            throw new BusinessException("Payments can only be refunded before the trip starts.");
        }
        payment.refund();
        paymentRepository.update(payment);
        if (rental != null) {
            rental.cancel();
            rentalRepository.update(rental);
        }
    }
}
