package com.driveeasy.rental.model.user;

import com.driveeasy.rental.util.FileUtil;

import java.time.LocalDate;

/**
 * A staff member who manages the system.
 * Inheritance: Admin IS-A User. Polymorphism: different home page and permissions.
 */
public class Admin extends User {

    public static final String ROLE = "ADMIN";

    private String jobTitle;

    public Admin(String id, String username, String passwordHash, String fullName, String email,
                 String phone, LocalDate registeredDate, String jobTitle) {
        super(id, username, passwordHash, fullName, email, phone, registeredDate);
        this.jobTitle = jobTitle;
    }

    @Override
    public String getRole() {
        return ROLE;
    }

    @Override
    public String getRoleDisplayName() {
        return "Administrator";
    }

    @Override
    public boolean isAdmin() {
        return true;
    }

    @Override
    public String getHomePage() {
        return "/admin";
    }

    /** Format: [User fields]|jobTitle */
    @Override
    public String toFileString() {
        return super.toFileString() + FileUtil.SEPARATOR + FileUtil.join(jobTitle);
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
