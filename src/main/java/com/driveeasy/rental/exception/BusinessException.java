package com.driveeasy.rental.exception;

/**
 * Thrown when a user action breaks a business rule
 * (for example booking a vehicle that is already reserved).
 * The message is shown to the user.
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }
}
