package com.movieapp.dao;

import java.util.List;

/**
 * Defines the standard CRUD contract that data-access classes implement.
 * @param <T> the model type this DAO manages (e.g. Movie)
 */
public interface Crud<T> {

    boolean add(T item);

    List<T> getAll();

    boolean update(T item);

    boolean delete(int id);
}