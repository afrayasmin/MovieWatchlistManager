package com.movieapp.controller;

import com.movieapp.Main;
import com.movieapp.dao.MovieDAO;
import com.movieapp.model.Movie;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class DashboardController {

    @FXML private Label totalMoviesLabel;
    @FXML private Label watchedLabel;
    @FXML private Label unwatchedLabel;
    @FXML private ListView<String> recentMoviesList;

    private final MovieDAO movieDAO = new MovieDAO();

    @FXML
    public void initialize() {
        loadStats();
        loadRecentMovies();
    }

    private void loadStats() {
        List<Movie> movies = movieDAO.getAllMovies();
        int total = movies.size();
        long watched = movies.stream().filter(m -> m.getStatus().equals("Watched")).count();
        long unwatched = total - watched;

        totalMoviesLabel.setText(String.valueOf(total));
        watchedLabel.setText(String.valueOf(watched));
        unwatchedLabel.setText(String.valueOf(unwatched));
    }

    private void loadRecentMovies() {
        List<Movie> movies = movieDAO.getAllMovies(); // already ordered by id DESC
        List<String> display = movies.stream()
                .limit(5)
                .map(m -> (m.isFavorite() ? "⭐ " : "") + m.getTitle() + "  •  " + m.getReleaseYear() + "  •  " + m.getStatus())
                .toList();

        if (display.isEmpty()) {
            recentMoviesList.setItems(FXCollections.observableArrayList("No movies yet — add your first one!"));
        } else {
            recentMoviesList.setItems(FXCollections.observableArrayList(display));
        }
    }

    @FXML
    private void handleAddMovie() {
        try {
            Main.switchScene("/com/movieapp/fxml/add-movie.fxml", "Add New Movie");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewMovies() {
        try {
            Main.switchScene("/com/movieapp/fxml/movie-list.fxml", "Movie List");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleStatistics() {
        try {
            Main.switchScene("/com/movieapp/fxml/statistics.fxml", "Movie Statistics");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}