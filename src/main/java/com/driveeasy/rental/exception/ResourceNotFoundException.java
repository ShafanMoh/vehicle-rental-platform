package com.driveeasy.rental.exception;

/**
 * Thrown when a record with the requested ID does not exist in the data files.
 */
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(String entityName, String id) {
        super(entityName + " with ID " + id + " was not found.");
    }
}
