package com.driveeasy.rental.service;

import com.driveeasy.rental.dto.VehicleForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.exception.ResourceNotFoundException;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.vehicle.Car;
import com.driveeasy.rental.model.vehicle.Motorbike;
import com.driveeasy.rental.model.vehicle.SUV;
import com.driveeasy.rental.model.vehicle.Van;
import com.driveeasy.rental.model.vehicle.Vehicle;
import com.driveeasy.rental.model.vehicle.VehicleStatus;
import com.driveeasy.rental.repository.RentalRepository;
import com.driveeasy.rental.repository.ReviewRepository;
import com.driveeasy.rental.repository.VehicleRepository;
import com.driveeasy.rental.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.Year;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Component 2 - Vehicle (Fleet) Management.
 * Create: add vehicle. Read: search / view. Update: edit details or status. Delete: remove vehicle.
 */
@Service
public class VehicleService {

    private static final int OLDEST_YEAR = 1990;

    private final VehicleRepository vehicleRepository;
    private final RentalRepository rentalRepository;
    private final ReviewRepository reviewRepository;

    public VehicleService(VehicleRepository vehicleRepository, RentalRepository rentalRepository,
                          ReviewRepository reviewRepository) {
        this.vehicleRepository = vehicleRepository;
        this.rentalRepository = rentalRepository;
        this.reviewRepository = reviewRepository;
    }

    // ---------- Create ----------

    public Vehicle create(VehicleForm form) {
        String plate = ValidationUtil.required(form.getPlateNumber(), "Plate number").toUpperCase();
        ensurePlateFree(plate, null);
        Vehicle vehicle = buildVehicle(null, form);
        return vehicleRepository.save(vehicle);
    }

    // ---------- Read ----------

    public Vehicle getById(String id) {
        return vehicleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Vehicle", id));
    }

    public List<Vehicle> getAll() {
        return vehicleRepository.findAll();
    }

    /**
     * Search the fleet.
     *
     * @param keyword       matches brand, model, plate or location
     * @param type          CAR, SUV, VAN, MOTORBIKE or empty for all
     * @param maxRate       highest daily rate, or null for any
     * @param availableOnly hide vehicles under maintenance
     * @param sort          price_asc, price_desc, newest or empty
     */
    public List<Vehicle> search(String keyword, String type, Double maxRate, boolean availableOnly, String sort) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase();
        Comparator<Vehicle> order = switch (sort == null ? "" : sort) {
            case "price_asc" -> Comparator.comparingDouble(Vehicle::getDailyRate);
            case "price_desc" -> Comparator.comparingDouble(Vehicle::getDailyRate).reversed();
            case "newest" -> Comparator.comparingInt(Vehicle::getYear).reversed();
            default -> Comparator.comparing(Vehicle::getId);
        };
        return vehicleRepository.findAll().stream()
                .filter(v -> type == null || type.isBlank() || v.getType().equals(type))
                .filter(v -> maxRate == null || v.getDailyRate() <= maxRate)
                .filter(v -> !availableOnly || v.isBookable())
                .filter(v -> query.isEmpty()
                        || v.getDisplayName().toLowerCase().contains(query)
                        || v.getPlateNumber().toLowerCase().contains(query)
                        || v.getLocation().toLowerCase().contains(query))
                .sorted(order)
                .toList();
    }

    public List<Vehicle> getFeatured(int limit) {
        return vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .limit(limit)
                .toList();
    }

    public Map<String, Vehicle> getVehicleMap() {
        return vehicleRepository.findAll().stream()
                .collect(Collectors.toMap(Vehicle::getId, Function.identity()));
    }

    /** Number of vehicles of each type, e.g. {Car=4, SUV=2}. */
    public Map<String, Long> countByType() {
        return vehicleRepository.findAll().stream()
                .collect(Collectors.groupingBy(Vehicle::getTypeDisplayName, LinkedHashMap::new,
                        Collectors.counting()));
    }

    public long countByStatus(VehicleStatus status) {
        return vehicleRepository.findAll().stream().filter(v -> v.getStatus() == status).count();
    }

    // ---------- Update ----------

    public Vehicle update(String id, VehicleForm form) {
        getById(id);
        String plate = ValidationUtil.required(form.getPlateNumber(), "Plate number").toUpperCase();
        ensurePlateFree(plate, id);
        Vehicle updated = buildVehicle(id, form);
        vehicleRepository.update(updated);
        return updated;
    }

    public void changeStatus(String id, VehicleStatus status) {
        Vehicle vehicle = getById(id);
        vehicle.setStatus(status);
        vehicleRepository.update(vehicle);
    }

    // ---------- Delete ----------

    public void delete(String id) {
        getById(id);
        boolean hasOpenBookings = rentalRepository.findByVehicleId(id).stream()
                .anyMatch(Rental::isBlockingVehicle);
        if (hasOpenBookings) {
            throw new BusinessException("This vehicle has open bookings and cannot be deleted.");
        }
        reviewRepository.findByVehicleId(id).forEach(review -> reviewRepository.deleteById(review.getId()));
        vehicleRepository.deleteById(id);
    }

    // ---------- Factory: picks the right subclass for the chosen type ----------

    private Vehicle buildVehicle(String id, VehicleForm form) {
        String brand = ValidationUtil.required(form.getBrand(), "Brand");
        String model = ValidationUtil.required(form.getModel(), "Model");
        String plate = form.getPlateNumber().trim().toUpperCase();
        int year = requireNumber(form.getYear(), "Year");
        if (year < OLDEST_YEAR || year > Year.now().getValue() + 1) {
            throw new BusinessException("Year must be between " + OLDEST_YEAR + " and " + (Year.now().getValue() + 1) + ".");
        }
        double rate = form.getDailyRate() == null ? 0 : form.getDailyRate();
        ValidationUtil.positive(rate, "Daily rate");
        int seats = requireNumber(form.getSeats(), "Seats");
        String fuel = ValidationUtil.required(form.getFuelType(), "Fuel type");
        VehicleStatus status = VehicleStatus.valueOf(form.getStatus());
        String image = form.getImageUrl() == null ? "" : form.getImageUrl().trim();
        String description = form.getDescription() == null ? "" : form.getDescription().trim();
        String location = ValidationUtil.required(form.getLocation(), "Location");

        return switch (form.getType()) {
            case Car.TYPE -> new Car(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, form.getTransmission());
            case SUV.TYPE -> new SUV(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, form.getTransmission(), form.isFourWheelDrive());
            case Van.TYPE -> new Van(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, requireNumber(form.getCargoCapacityKg(), "Cargo capacity"));
            case Motorbike.TYPE -> new Motorbike(id, brand, model, year, plate, rate, seats, fuel, status, image,
                    description, location, requireNumber(form.getEngineCc(), "Engine capacity"),
                    form.isHelmetIncluded());
            default -> throw new BusinessException("Please choose a vehicle type.");
        };
    }

    private int requireNumber(Integer value, String fieldName) {
        if (value == null || value <= 0) {
            throw new BusinessException(fieldName + " must be greater than zero.");
        }
        return value;
    }

    private void ensurePlateFree(String plate, String ownId) {
        vehicleRepository.findByPlateNumber(plate)
                .filter(other -> !other.getId().equals(ownId))
                .ifPresent(other -> {
                    throw new BusinessException("A vehicle with plate number " + plate + " already exists.");
                });
    }
}
