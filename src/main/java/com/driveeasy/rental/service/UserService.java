package com.driveeasy.rental.service;

import com.driveeasy.rental.dto.UserForm;
import com.driveeasy.rental.exception.BusinessException;
import com.driveeasy.rental.exception.ResourceNotFoundException;
import com.driveeasy.rental.model.rental.Rental;
import com.driveeasy.rental.model.user.Admin;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.PremiumCustomer;
import com.driveeasy.rental.model.user.User;
import com.driveeasy.rental.repository.RentalRepository;
import com.driveeasy.rental.repository.UserRepository;
import com.driveeasy.rental.util.PasswordUtil;
import com.driveeasy.rental.util.ValidationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Component 1 - User Management.
 * Create: register / add user. Read: login, search, view. Update: profile, password, membership.
 * Delete: remove account.
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;

    public UserService(UserRepository userRepository, RentalRepository rentalRepository) {
        this.userRepository = userRepository;
        this.rentalRepository = rentalRepository;
    }

    // ---------- Create ----------

    /** Public sign-up. New accounts are always regular customers. */
    public Customer register(UserForm form) {
        form.setRole(Customer.ROLE);
        return (Customer) createUser(form);
    }

    /** Admin can create customers, premium members or other admins. */
    public User createUser(UserForm form) {
        String username = ValidationUtil.username(form.getUsername());
        String password = ValidationUtil.password(form.getPassword());
        if (!password.equals(form.getConfirmPassword())) {
            throw new BusinessException("Passwords do not match.");
        }
        String email = ValidationUtil.email(form.getEmail());
        ensureUsernameFree(username, null);
        ensureEmailFree(email, null);

        String hash = PasswordUtil.hash(password);
        String fullName = ValidationUtil.required(form.getFullName(), "Full name");
        String phone = ValidationUtil.phone(form.getPhone());
        LocalDate today = LocalDate.now();

        User user = switch (form.getRole()) {
            case Admin.ROLE -> new Admin(null, username, hash, fullName, email, phone, today,
                    ValidationUtil.required(form.getJobTitle(), "Job title"));
            case PremiumCustomer.ROLE -> new PremiumCustomer(null, username, hash, fullName, email, phone, today,
                    ValidationUtil.required(form.getDrivingLicenseNumber(), "Driving licence number"),
                    ValidationUtil.required(form.getAddress(), "Address"), 0);
            default -> new Customer(null, username, hash, fullName, email, phone, today,
                    ValidationUtil.required(form.getDrivingLicenseNumber(), "Driving licence number"),
                    ValidationUtil.required(form.getAddress(), "Address"));
        };
        return userRepository.save(user);
    }

    // ---------- Read ----------

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username == null ? "" : username.trim())
                .orElseThrow(() -> new BusinessException("Invalid username or password."));
        if (!user.authenticate(password)) {
            throw new BusinessException("Invalid username or password.");
        }
        return user;
    }

    public User getById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    public Customer getCustomer(String id) {
        if (getById(id) instanceof Customer customer) {
            return customer;
        }
        throw new BusinessException("Only customers can do this. Please log in with a customer account.");
    }

    /** The logged-in customer, or null if nobody is logged in or the user is an admin. */
    public Customer findCustomerOrNull(String userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .filter(Customer.class::isInstance)
                .map(Customer.class::cast)
                .orElse(null);
    }

    /** Search by name, username, email or phone, optionally filtered by role. */
    public List<User> search(String keyword, String role) {
        String query = keyword == null ? "" : keyword.trim().toLowerCase();
        return userRepository.findAll().stream()
                .filter(user -> role == null || role.isBlank() || user.getRole().equals(role))
                .filter(user -> query.isEmpty()
                        || user.getFullName().toLowerCase().contains(query)
                        || user.getUsername().contains(query)
                        || user.getEmail().contains(query)
                        || user.getPhone().contains(query)
                        || user.getId().equalsIgnoreCase(query))
                .sorted(Comparator.comparing(User::getId))
                .toList();
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Map<String, User> getUserMap() {
        return userRepository.findAll().stream().collect(Collectors.toMap(User::getId, Function.identity()));
    }

    // ---------- Update ----------

    /** A user edits their own details. Username and role cannot be changed here. */
    public User updateProfile(String userId, UserForm form) {
        User user = getById(userId);
        applyCommonDetails(user, form);
        userRepository.update(user);
        return user;
    }

    public void changePassword(String userId, String currentPassword, String newPassword, String confirm) {
        User user = getById(userId);
        if (!user.authenticate(currentPassword)) {
            throw new BusinessException("Current password is incorrect.");
        }
        String password = ValidationUtil.password(newPassword);
        if (!password.equals(confirm)) {
            throw new BusinessException("New passwords do not match.");
        }
        user.changePassword(password);
        userRepository.update(user);
    }

    /** Admin edit: details, membership type and optional password reset. */
    public User updateByAdmin(String userId, UserForm form) {
        User user = getById(userId);
        applyCommonDetails(user, form);

        if (form.getPassword() != null && !form.getPassword().isBlank()) {
            user.changePassword(ValidationUtil.password(form.getPassword()));
        }
        User updated = changeMembership(user, form.getRole());
        userRepository.update(updated);
        return updated;
    }

    /** Swaps a customer between Regular and Premium by creating an object of the other class. */
    private User changeMembership(User user, String newRole) {
        if (newRole == null || newRole.equals(user.getRole()) || !(user instanceof Customer customer)) {
            return user;
        }
        if (PremiumCustomer.ROLE.equals(newRole)) {
            return new PremiumCustomer(customer);
        }
        if (Customer.ROLE.equals(newRole)) {
            return new Customer(customer);
        }
        throw new BusinessException("A customer account cannot be turned into an admin account.");
    }

    private void applyCommonDetails(User user, UserForm form) {
        String email = ValidationUtil.email(form.getEmail());
        ensureEmailFree(email, user.getId());
        user.setFullName(ValidationUtil.required(form.getFullName(), "Full name"));
        user.setEmail(email);
        user.setPhone(ValidationUtil.phone(form.getPhone()));

        if (user instanceof Customer customer) {
            customer.setDrivingLicenseNumber(
                    ValidationUtil.required(form.getDrivingLicenseNumber(), "Driving licence number"));
            customer.setAddress(ValidationUtil.required(form.getAddress(), "Address"));
        } else if (user instanceof Admin admin) {
            admin.setJobTitle(ValidationUtil.required(form.getJobTitle(), "Job title"));
        }
    }

    /** Saves a user object that another service changed (e.g. loyalty points). */
    public void save(User user) {
        userRepository.update(user);
    }

    // ---------- Delete ----------

    public void delete(String userId) {
        User user = getById(userId);
        boolean hasOpenBookings = rentalRepository.findByCustomerId(userId).stream()
                .anyMatch(Rental::isBlockingVehicle);
        if (hasOpenBookings) {
            throw new BusinessException("This account has open bookings. Cancel or complete them first.");
        }
        if (user.isAdmin() && countAdmins() <= 1) {
            throw new BusinessException("You cannot delete the last admin account.");
        }
        userRepository.deleteById(userId);
    }

    // ---------- Helpers ----------

    public long countCustomers() {
        return userRepository.findAll().stream().filter(user -> !user.isAdmin()).count();
    }

    private long countAdmins() {
        return userRepository.findAll().stream().filter(User::isAdmin).count();
    }

    private void ensureUsernameFree(String username, String ownId) {
        userRepository.findByUsername(username)
                .filter(other -> !other.getId().equals(ownId))
                .ifPresent(other -> {
                    throw new BusinessException("Username '" + username + "' is already taken.");
                });
    }

    private void ensureEmailFree(String email, String ownId) {
        userRepository.findByEmail(email)
                .filter(other -> !other.getId().equals(ownId))
                .ifPresent(other -> {
                    throw new BusinessException("An account with this email already exists.");
                });
    }
}
