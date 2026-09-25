package com.movieapp.dao;

import com.movieapp.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base class for DAO classes. Holds the connection-acquisition and
 * error-logging pattern that every DAO in this app repeats, so subclasses
 * only need to focus on their own SQL.
 */
public abstract class BaseDAO {

    protected Connection connect() throws SQLException {
        return DatabaseConnection.connect();
    }

    protected void logError(String action, SQLException e) {
        System.out.println("Error " + action + ": " + e.getMessage());
    }
}