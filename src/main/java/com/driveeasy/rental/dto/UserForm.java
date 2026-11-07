package com.driveeasy.rental.dto;

import com.driveeasy.rental.model.user.Admin;
import com.driveeasy.rental.model.user.Customer;
import com.driveeasy.rental.model.user.User;

/**
 * Holds the values typed into the registration, profile and admin user forms.
 */
public class UserForm {

    private String role = Customer.ROLE;
    private String username;
    private String password;
    private String confirmPassword;
    private String fullName;
    private String email;
    private String phone;
    private String drivingLicenseNumber;
    private String address;
    private String jobTitle;

    /** Fills the form with an existing user's details (for edit pages). */
    public static UserForm fromUser(User user) {
        UserForm form = new UserForm();
        form.role = user.getRole();
        form.username = user.getUsername();
        form.fullName = user.getFullName();
        form.email = user.getEmail();
        form.phone = user.getPhone();
        if (user instanceof Customer customer) {
            form.drivingLicenseNumber = customer.getDrivingLicenseNumber();
            form.address = customer.getAddress();
        } else if (user instanceof Admin admin) {
            form.jobTitle = admin.getJobTitle();
        }
        return form;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
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

    public String getDrivingLicenseNumber() {
        return drivingLicenseNumber;
    }

    public void setDrivingLicenseNumber(String drivingLicenseNumber) {
        this.drivingLicenseNumber = drivingLicenseNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
}
