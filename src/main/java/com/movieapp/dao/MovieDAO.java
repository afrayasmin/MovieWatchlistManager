package com.movieapp.dao;

import com.movieapp.database.DatabaseConnection;
import com.movieapp.model.Movie;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MovieDAO implements Crud<Movie> {

    private final GenreDAO genreDAO = new GenreDAO();

    // Shared SELECT clause used by every read method: joins movies to
    // genres so the genre's name is available under the alias "genre",
    // keeping mapRowToMovie() unchanged.
    private static final String BASE_SELECT = """
        SELECT m.id, m.title, g.name AS genre, m.release_year, m.rating,
               m.my_rating, m.date_added, m.status, m.notes, m.favorite
        FROM movies m
        JOIN genres g ON m.genre_id = g.id
        """;

    // ===== Crud<Movie> implementation =====

    // CREATE
    @Override
    public boolean add(Movie movie) {
        String sql = "INSERT INTO movies (title, genre_id, release_year, rating, my_rating, date_added, status, notes, favorite) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.connect()) {
            int genreId = genreDAO.resolveGenreId(conn, movie.getGenre());

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, movie.getTitle());
                ps.setInt(2, genreId);
                ps.setInt(3, movie.getReleaseYear());
                ps.setDouble(4, movie.getRating());
                ps.setDouble(5, movie.getMyRating());
                ps.setString(6, movie.getDateAdded());
                ps.setString(7, movie.getStatus());
                ps.setString(8, movie.getNotes());
                ps.setInt(9, movie.isFavorite() ? 1 : 0);

                ps.executeUpdate();
                return true;
            }

        } catch (SQLException e) {
            System.out.println("Error adding movie: " + e.getMessage());
            return false;
        }
    }

    // READ — all movies
    @Override
    public List<Movie> getAll() {
        List<Movie> movies = new ArrayList<>();
        String sql = BASE_SELECT + " ORDER BY m.id DESC";

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

    // UPDATE
    @Override
    public boolean update(Movie movie) {
        String sql = "UPDATE movies SET title = ?, genre_id = ?, release_year = ?, rating = ?, my_rating = ?, " +
                "status = ?, notes = ?, favorite = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect()) {
            int genreId = genreDAO.resolveGenreId(conn, movie.getGenre());

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, movie.getTitle());
                ps.setInt(2, genreId);
                ps.setInt(3, movie.getReleaseYear());
                ps.setDouble(4, movie.getRating());
                ps.setDouble(5, movie.getMyRating());
                ps.setString(6, movie.getStatus());
                ps.setString(7, movie.getNotes());
                ps.setInt(8, movie.isFavorite() ? 1 : 0);
                ps.setInt(9, movie.getId());

                int rows = ps.executeUpdate();
                return rows > 0;
            }

        } catch (SQLException e) {
            System.out.println("Error updating movie: " + e.getMessage());
            return false;
        }
    }

    // DELETE
    @Override
    public boolean delete(int id) {
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

    // ===== Backward-compatible convenience wrappers =====
    // Kept so existing controller code (addMovie(), getAllMovies(), etc.)
    // doesn't need to change. They simply delegate to the interface methods.

    public boolean addMovie(Movie movie) {
        return add(movie);
    }

    public List<Movie> getAllMovies() {
        return getAll();
    }

    public boolean updateMovie(Movie movie) {
        return update(movie);
    }

    public boolean deleteMovie(int id) {
        return delete(id);
    }

    // ===== Movie-specific queries (not part of the generic CRUD contract) =====

    // READ — search by title
    public List<Movie> searchMovies(String keyword) {
        List<Movie> movies = new ArrayList<>();
        String sql = BASE_SELECT + " WHERE m.title LIKE ? ORDER BY m.id DESC";

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
        StringBuilder sql = new StringBuilder(BASE_SELECT + " WHERE 1=1");

        if (genre != null && !genre.equals("All")) {
            sql.append(" AND g.name = ?");
        }
        if (status != null && !status.equals("All")) {
            sql.append(" AND m.status = ?");
        }
        if (favoritesOnly) {
            sql.append(" AND m.favorite = 1");
        }
        sql.append(" ORDER BY m.id DESC");

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

    // Toggle to Unwatched — always clears the personal rating.
    public boolean toggleStatus(int id, String newStatus) {
        boolean clearingRating = "Unwatched".equals(newStatus);
        String sql = clearingRating
                ? "UPDATE movies SET status = ?, my_rating = 0 WHERE id = ?"
                : "UPDATE movies SET status = ? WHERE id = ?";

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

    // Mark a movie as Watched AND set its personal rating in one step.
    public boolean markWatchedWithRating(int id, double myRating) {
        String sql = "UPDATE movies SET status = 'Watched', my_rating = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, myRating);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.out.println("Error marking movie watched: " + e.getMessage());
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

    // Helper — converts one ResultSet row into a Movie object.
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