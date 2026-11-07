package com.driveeasy.rental.model;

/**
 * Every record saved in a text file has a unique ID.
 */
public interface Identifiable {

    String getId();

    void setId(String id);
}
