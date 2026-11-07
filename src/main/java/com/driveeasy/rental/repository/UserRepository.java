package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.user.Admin;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.PremiumCustomer;
import com.driveeasy.rental.model.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Stores customers and admins in users.txt.
 * The first field of each line (the role) decides which class is created.
 */
@Repository
public class UserRepository extends FileRepository<User> {

    public UserRepository(@Value("${app.data-dir}") String dataDirectory) {
        super(dataDirectory, "users.txt", "U");
    }

    @Override
    protected User parse(String[] f) {
        String role = f[0];
        String id = f[1];
        String username = f[2];
        String passwordHash = f[3];
        String fullName = f[4];
        String email = f[5];
        String phone = f[6];
        LocalDate registered = toDate(f[7]);

        return switch (role) {
            case Admin.ROLE -> new Admin(id, username, passwordHash, fullName, email, phone, registered,
                    field(f, 8));
            case PremiumCustomer.ROLE -> new PremiumCustomer(id, username, passwordHash, fullName, email, phone,
                    registered, field(f, 8), field(f, 9), toInt(field(f, 10)));
            case Customer.ROLE -> new Customer(id, username, passwordHash, fullName, email, phone, registered,
                    field(f, 8), field(f, 9));
            default -> throw new IllegalArgumentException("Unknown role " + role);
        };
    }

    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
