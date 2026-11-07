package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.vehicle.Car;
import com.driveeasy.rental.model.vehicle.Motorbike;
import com.driveeasy.rental.model.vehicle.SUV;
import com.driveeasy.rental.model.vehicle.Van;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Stores the fleet in vehicles.txt.
 * The first field (the vehicle type) decides which subclass is created.
 */
@Repository
public class VehicleRepository extends FileRepository<Vehicle> {

    public VehicleRepository(@Value("${app.data-dir}") String dataDirectory) {
        super(dataDirectory, "vehicles.txt", "V");
    }

    @Override
    protected Vehicle parse(String[] f) {
        String type = f[0];
        String id = f[1];
        String brand = f[2];
        String model = f[3];
        int year = toInt(f[4]);
        String plate = f[5];
        double rate = toDouble(f[6]);
        int seats = toInt(f[7]);
        String fuel = f[8];
        VehicleStatus status = VehicleStatus.valueOf(f[9]);
        String image = f[10];
        String description = f[11];
        String location = f[12];

        return switch (type) {
            case Car.TYPE -> new Car(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, field(f, 13));
            case SUV.TYPE -> new SUV(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, field(f, 13), toBoolean(field(f, 14)));
            case Van.TYPE -> new Van(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, toInt(field(f, 13)));
            case Motorbike.TYPE -> new Motorbike(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, toInt(field(f, 13)), toBoolean(field(f, 14)));
            default -> throw new IllegalArgumentException("Unknown vehicle type " + type);
        };
    }

    public Optional<Vehicle> findByPlateNumber(String plateNumber) {
        return findAll().stream()
                .filter(vehicle -> vehicle.getPlateNumber().equalsIgnoreCase(plateNumber))
                .findFirst();
    }
}
