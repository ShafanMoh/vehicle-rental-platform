package com.driveeasy.rental.util;

import com.driveeasy.rental.exception.BusinessException;

import java.util.regex.Pattern;

/**
 * Simple input checks shared by the services.
 */
public final class ValidationUtil {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?\\d{9,12}$");
    private static final Pattern USERNAME = Pattern.compile("^[a-zA-Z0-9_]{4,20}$");

    private ValidationUtil() {
    }

    public static String required(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(fieldName + " is required.");
        }
        return value.trim();
    }

    public static String email(String value) {
        String email = required(value, "Email");
        if (!EMAIL.matcher(email).matches()) {
            throw new BusinessException("Please enter a valid email address.");
        }
        return email.toLowerCase();
    }

    public static String phone(String value) {
        String phone = required(value, "Phone number").replace(" ", "");
        if (!PHONE.matcher(phone).matches()) {
            throw new BusinessException("Phone number must have 9 to 12 digits.");
        }
        return phone;
    }

    public static String username(String value) {
        String username = required(value, "Username");
        if (!USERNAME.matcher(username).matches()) {
            throw new BusinessException("Username must be 4-20 letters, numbers or underscores.");
        }
        return username.toLowerCase();
    }

    public static String password(String value) {
        String password = required(value, "Password");
        if (password.length() < 6) {
            throw new BusinessException("Password must be at least 6 characters.");
        }
        return password;
    }

    public static void positive(double value, String fieldName) {
        if (value <= 0) {
            throw new BusinessException(fieldName + " must be greater than zero.");
        }
    }
}
