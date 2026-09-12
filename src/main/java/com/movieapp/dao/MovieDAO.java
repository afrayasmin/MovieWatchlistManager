package com.movieapp.dao;

import com.movieapp.database.DatabaseConnection;
import com.movieapp.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO {

    // CREATE
    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO movies (title, genre, release_year, rating, my_rating, date_added, status, notes, favorite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            ps.setInt(3, movie.getReleaseYear());
            ps.setDouble(4, movie.getRating());
            ps.setDouble(5, movie.getMyRating());
            ps.setString(6, movie.getDateAdded());
            ps.setString(7, movie.getStatus());
            ps.setString(8, movie.getNotes());
            ps.setInt(9, movie.isFavorite() ? 1 : 0);

            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error adding movie: " + e.getMessage());
            return false;
        }
    }

    // READ — all movies
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                movies.add(mapRowToMovie(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching movies: " + e.getMessage());
        }

        return movies;
    }

    // READ — search by title
    public List<Movie> searchMovies(String keyword) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies WHERE title LIKE ? ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                movies.add(mapRowToMovie(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error searching movies: " + e.getMessage());
        }

        return movies;
    }

    // READ — filter by genre and/or status and/or favorites
    public List<Movie> filterMovies(String genre, String status, boolean favoritesOnly) {
        List<Movie> movies = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM movies WHERE 1=1");

        if (genre != null && !genre.equals("All")) {
            sql.append(" AND genre = ?");
        }
        if (status != null && !status.equals("All")) {
            sql.append(" AND status = ?");
        }
        if (favoritesOnly) {
            sql.append(" AND favorite = 1");
        }
        sql.append(" ORDER BY id DESC");

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int index = 1;
            if (genre != null && !genre.equals("All")) {
                ps.setString(index++, genre);
            }
            if (status != null && !status.equals("All")) {
                ps.setString(index++, status);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                movies.add(mapRowToMovie(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error filtering movies: " + e.getMessage());
        }

        return movies;
    }

    // UPDATE
    public boolean updateMovie(Movie movie) {
        String sql = "UPDATE movies SET title = ?, genre = ?, release_year = ?, rating = ?, my_rating = ?, " +
                "status = ?, notes = ?, favorite = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, movie.getTitle());
            ps.setString(2, movie.getGenre());
            ps.setInt(3, movie.getReleaseYear());
            ps.setDouble(4, movie.getRating());
            ps.setDouble(5, movie.getMyRating());
            ps.setString(6, movie.getStatus());
            ps.setString(7, movie.getNotes());
            ps.setInt(8, movie.isFavorite() ? 1 : 0);
            ps.setInt(9, movie.getId());

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating movie: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    public boolean deleteMovie(int id) {
        String sql = "DELETE FROM movies WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting movie: " + e.getMessage());
            return false;
        }
    }

    // Toggle watched/unwatched
    public boolean toggleStatus(int id, String newStatus) {
        String sql = "UPDATE movies SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, newStatus);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    // Toggle favorite on/off
    public boolean toggleFavorite(int id, boolean newFavorite) {
        String sql = "UPDATE movies SET favorite = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newFavorite ? 1 : 0);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error updating favorite: " + e.getMessage());
            return false;
        }
    }

    // Helper — converts one ResultSet row into a Movie object
    private Movie mapRowToMovie(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("genre"),
                rs.getInt("release_year"),
                rs.getDouble("rating"),
                rs.getDouble("my_rating"),
                rs.getString("date_added"),
                rs.getString("status"),
                rs.getString("notes"),
                rs.getInt("favorite") == 1
        );
    }
}