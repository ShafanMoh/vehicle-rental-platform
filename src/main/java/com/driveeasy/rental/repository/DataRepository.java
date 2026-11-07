package com.driveeasy.rental.repository;

import com.driveeasy.rental.model.Identifiable;

import java.util.List;
import java.util.Optional;

/**
 * Abstraction of the CRUD operations every repository must offer.
 * The services depend on this contract, not on how data is stored.
 *
 * @param <T> type of record stored
 */
public interface DataRepository<T extends Identifiable> {

    /** Create: saves a new record and gives it an ID. */
    T save(T entity);

    /** Read: returns every record. */
    List<T> findAll();

    /** Read: finds one record by its ID. */
    Optional<T> findById(String id);

    /** Update: replaces the stored record that has the same ID. */
    boolean update(T entity);

    /** Delete: removes the record with this ID. */
    boolean deleteById(String id);
}
