package com.movieapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    // This creates a file called movies.db in your project's root folder
    private static final String URL = "jdbc:sqlite:movies.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
        return conn;
    }

    // Call this once at app startup to make sure the table exists
    public static void initializeDatabase() {
        String createTableSql = """
        CREATE TABLE IF NOT EXISTS movies (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            title TEXT NOT NULL,
            genre TEXT,
            release_year INTEGER,
            rating REAL,
            date_added TEXT,
            status TEXT,
            notes TEXT
        );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSql);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }

        // Migration: add 'favorite' column if missing
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE movies ADD COLUMN favorite INTEGER DEFAULT 0");
            System.out.println("Added 'favorite' column.");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                System.out.println("Note: " + e.getMessage());
            }
        }

        // Migration: add 'my_rating' column if missing
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE movies ADD COLUMN my_rating REAL DEFAULT 0");
            System.out.println("Added 'my_rating' column.");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                System.out.println("Note: " + e.getMessage());
            }
        }
    }
}