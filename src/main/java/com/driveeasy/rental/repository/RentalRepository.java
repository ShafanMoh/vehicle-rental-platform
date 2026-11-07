package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.rental.RentalStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Stores bookings in rentals.txt.
 */
@Repository
public class RentalRepository extends FileRepository<Rental> {

    public RentalRepository(@Value("${app.data-dir}") String dataDirectory) {
        super(dataDirectory, "rentals.txt", "R");
    }

    @Override
    protected Rental parse(String[] f) {
        return new Rental(f[0], f[1], f[2], toDate(f[3]), toDate(f[4]), f[5], RentalStatus.valueOf(f[6]),
                toDouble(f[7]), toDouble(f[8]), toDouble(f[9]), toDate(f[10]));
    }

    public List<Rental> findByCustomerId(String customerId) {
        return findAll().stream()
                .filter(rental -> rental.getCustomerId().equals(customerId))
                .toList();
    }

    public List<Rental> findByVehicleId(String vehicleId) {
        return findAll().stream()
                .filter(rental -> rental.getVehicleId().equals(vehicleId))
                .toList();
    }
}
