package com.driveeasy.rental.model.user;

import com.driveeasy.rental.model.FileStorable;
import com.driveeasy.rental.model.Identifiable;
import com.driveeasy.rental.util.FileUtil;
import com.driveeasy.rental.util.PasswordUtil;

import java.time.LocalDate;

/**
 * Base class for every person who can log in.
 *
 * OOP concepts:
 *  - Abstraction: User is abstract; only Customer, PremiumCustomer and Admin objects exist.
 *  - Encapsulation: all fields are private and reached through getters/setters.
 *  - Information hiding: the password hash has no getter. Other classes can only
 *    call authenticate() or changePassword().
 */
public abstract class User implements Identifiable, FileStorable {

    private String id;
    private String username;
    private String passwordHash;
    private String fullName;
    private String email;
    private String phone;
    private LocalDate registeredDate;

    protected User(String id, String username, String passwordHash, String fullName,
                   String email, String phone, LocalDate registeredDate) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.registeredDate = registeredDate;
    }

    /** Copy constructor, used when a user's type changes (e.g. Customer to PremiumCustomer). */
    protected User(User other) {
        this(other.id, other.username, other.passwordHash, other.fullName,
                other.email, other.phone, other.registeredDate);
    }

    // ---------- Abstract methods: each subclass gives its own answer ----------

    /** Code saved in the file, e.g. CUSTOMER, PREMIUM, ADMIN. */
    public abstract String getRole();

    /** Friendly name shown in the UI. */
    public abstract String getRoleDisplayName();

    public abstract boolean isAdmin();

    /** The page a user is sent to after logging in. */
    public abstract String getHomePage();

    // ---------- Behaviour ----------

    /** Checks a typed password against the stored hash. */
    public boolean authenticate(String rawPassword) {
        return PasswordUtil.matches(rawPassword, passwordHash);
    }

    public void changePassword(String newRawPassword) {
        this.passwordHash = PasswordUtil.hash(newRawPassword);
    }

    public String getInitials() {
        if (fullName == null || fullName.isBlank()) {
            return "?";
        }
        String[] parts = fullName.trim().split("\\s+");
        String initials = parts[0].substring(0, 1);
        if (parts.length > 1) {
            initials += parts[parts.length - 1].substring(0, 1);
        }
        return initials.toUpperCase();
    }

    /**
     * Writes the common fields. Subclasses add their own fields after these.
     * Format: role|id|username|passwordHash|fullName|email|phone|registeredDate
     */
    @Override
    public String toFileString() {
        return FileUtil.join(getRole(), id, username, passwordHash, fullName, email, phone, registeredDate);
    }

    // ---------- Getters and setters ----------

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getRegisteredDate() {
        return registeredDate;
    }
}
