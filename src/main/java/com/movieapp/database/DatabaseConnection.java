package com.movieapp.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

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

        // ---- Relational upgrade: genres table ----

        // Step 1: create the genres table (primary key = id)
        String createGenresSql = """
        CREATE TABLE IF NOT EXISTS genres (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT UNIQUE NOT NULL
        );
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createGenresSql);
            System.out.println("Genres table created (or already existed).");
        } catch (SQLException e) {
            System.out.println("Error creating genres table: " + e.getMessage());
        }

        // Step 2: seed it with the genres the app has always offered.
        // INSERT OR IGNORE means re-running this on an existing DB is safe —
        // it won't create duplicates thanks to the UNIQUE constraint on name.
        String[] defaultGenres = {
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Animation", "Romantic"
        };

        try (Connection conn = connect();
             java.sql.PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR IGNORE INTO genres (name) VALUES (?)")) {
            for (String genreName : defaultGenres) {
                ps.setString(1, genreName);
                ps.executeUpdate();
            }
            System.out.println("Default genres seeded.");
        } catch (SQLException e) {
            System.out.println("Error seeding genres: " + e.getMessage());
        }

        // Step 3: add the foreign key column to movies, linking to genres.id
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("ALTER TABLE movies ADD COLUMN genre_id INTEGER REFERENCES genres(id)");
            System.out.println("Added 'genre_id' column (foreign key to genres).");
        } catch (SQLException e) {
            if (!e.getMessage().contains("duplicate column")) {
                System.out.println("Note: " + e.getMessage());
            }
        }

        // Step 4: migrate existing data — for every movie whose genre_id is
        // still empty, look up the matching genres.id from its old text
        // genre column and fill it in. Safe to re-run: only touches rows
        // that haven't been migrated yet.
        String migrateSql = """
        UPDATE movies
        SET genre_id = (SELECT id FROM genres WHERE genres.name = movies.genre)
        WHERE genre_id IS NULL AND genre IS NOT NULL;
        """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            int rowsMigrated = stmt.executeUpdate(migrateSql);
            System.out.println("Migrated " + rowsMigrated + " existing movie(s) to use genre_id.");
        } catch (SQLException e) {
            System.out.println("Error migrating genre data: " + e.getMessage());
        }

        // Step 5: index on genre_id, since every movie query now joins on it
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE INDEX IF NOT EXISTS idx_movies_genre_id ON movies(genre_id)");
            System.out.println("Index on genre_id ready.");
        } catch (SQLException e) {
            System.out.println("Error creating index: " + e.getMessage());
        }
    }
}