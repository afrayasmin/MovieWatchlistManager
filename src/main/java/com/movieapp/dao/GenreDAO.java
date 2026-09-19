package com.movieapp.dao;

import com.movieapp.database.DatabaseConnection;
import com.movieapp.model.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GenreDAO {

    // READ — all genres
    public List<Genre> getAllGenres() {
        List<Genre> genres = new ArrayList<>();
        String sql = "SELECT * FROM genres ORDER BY name";

        try (Connection conn = DatabaseConnection.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                genres.add(new Genre(rs.getInt("id"), rs.getString("name")));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching genres: " + e.getMessage());
        }

        return genres;
    }

    // Look up a genre's id by name. If it doesn't exist yet, create it.
    // This is what lets MovieDAO resolve a genre name (e.g. "Action") from
    // the Add/Edit form into the correct foreign key value to store.
    public int resolveGenreId(Connection conn, String genreName) throws SQLException {
        String selectSql = "SELECT id FROM genres WHERE name = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectSql)) {
            ps.setString(1, genreName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }

        String insertSql = "INSERT INTO genres (name) VALUES (?)";
        try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, genreName);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        }

        throw new SQLException("Failed to resolve or create genre: " + genreName);
    }
}