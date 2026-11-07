package com.driveeasy.rental.model;

/**
 * Abstraction for any object that can be written to a text file as one line.
 * Each class decides for itself how its fields are written (polymorphism).
 */
public interface FileStorable {

    String toFileString();
}
