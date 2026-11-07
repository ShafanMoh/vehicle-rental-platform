package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.payment.BankTransferPayment;
import com.driveeasy.rental.model.payment.CardPayment;
import com.driveeasy.rental.model.payment.CashPayment;
import com.driveeasy.rental.model.payment.Payment;
import com.driveeasy.rental.model.payment.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Stores payments in payments.txt.
 * The first field (payment method) decides which subclass is created.
 */
@Repository
public class PaymentRepository extends FileRepository<Payment> {

    public PaymentRepository(@Value("${app.data-dir}") String dataDirectory) {
        super(dataDirectory, "payments.txt", "P");
    }

    @Override
    protected Payment parse(String[] f) {
        String method = f[0];
        String id = f[1];
        String rentalId = f[2];
        String customerId = f[3];
        double amount = toDouble(f[4]);
        LocalDateTime createdAt = toDateTime(f[5]);
        PaymentStatus status = PaymentStatus.valueOf(f[6]);

        return switch (method) {
            case CardPayment.METHOD -> new CardPayment(id, rentalId, customerId, amount, createdAt, status,
                    field(f, 7), field(f, 8));
            case BankTransferPayment.METHOD -> new BankTransferPayment(id, rentalId, customerId, amount,
                    createdAt, status, field(f, 7), field(f, 8));
            case CashPayment.METHOD -> new CashPayment(id, rentalId, customerId, amount, createdAt, status);
            default -> throw new IllegalArgumentException("Unknown payment method " + method);
        };
    }

    public List<Payment> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(payment -> payment.getCustomerId().equals(customerId))
                .toList();
    }

    /** The current (not refunded) payment for a booking, if there is one. */
    public Optional<Payment> findActiveByRentalId(String rentalId) {
        return findAll().stream()
                .filter(payment -> payment.getRentalId().equals(rentalId))
                .filter(payment -> payment.getStatus() != PaymentStatus.REFUNDED)
                .findFirst();
    }
}
