package com.movieapp.controller;

import com.movieapp.Main;
import com.movieapp.dao.MovieDAO;
import com.movieapp.model.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsController {

    @FXML private Label totalLabel;
    @FXML private Label watchedLabel;
    @FXML private Label unwatchedLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label avgMyRatingLabel;
    @FXML private Label commonGenreLabel;
    @FXML private Label highestRatedLabel;
    @FXML private PieChart statusPieChart;

    private final MovieDAO movieDAO = new MovieDAO();

    @FXML
    public void initialize() {
        List<Movie> movies = movieDAO.getAllMovies();

        if (movies.isEmpty()) {
            totalLabel.setText("Total Movies: 0");
            watchedLabel.setText("Watched: 0");
            unwatchedLabel.setText("Unwatched: 0");
            avgRatingLabel.setText("Average IMDb Rating: N/A");
            avgMyRatingLabel.setText("Average My Rating: N/A");
            commonGenreLabel.setText("Most Common Genre: N/A");
            highestRatedLabel.setText("Highest Rated: N/A");
            return;
        }

        int total = movies.size();
        long watched = movies.stream().filter(m -> m.getStatus().equals("Watched")).count();
        long unwatched = total - watched;

        double avgRating = movies.stream().mapToDouble(Movie::getRating).average().orElse(0);

        double avgMyRating = movies.stream()
                .filter(m -> m.getMyRating() > 0)
                .mapToDouble(Movie::getMyRating)
                .average()
                .orElse(0);
        long ratedCount = movies.stream().filter(m -> m.getMyRating() > 0).count();

        Map<String, Long> genreCounts = movies.stream()
                .collect(Collectors.groupingBy(Movie::getGenre, Collectors.counting()));
        String mostCommonGenre = genreCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("N/A");

        Movie highestRated = movies.stream()
                .max((a, b) -> Double.compare(a.getRating(), b.getRating()))
                .orElse(null);

        totalLabel.setText("Total Movies: " + total);
        watchedLabel.setText("Watched: " + watched);
        unwatchedLabel.setText("Unwatched: " + unwatched);
        avgRatingLabel.setText(String.format("Average IMDb Rating: %.2f", avgRating));

        if (ratedCount > 0) {
            avgMyRatingLabel.setText(String.format("Average My Rating: %.2f (%d rated)", avgMyRating, ratedCount));
        } else {
            avgMyRatingLabel.setText("Average My Rating: No personal ratings yet");
        }

        commonGenreLabel.setText("Most Common Genre: " + mostCommonGenre);
        highestRatedLabel.setText("Highest Rated: " +
                (highestRated != null ? highestRated.getTitle() + " (" + highestRated.getRating() + ")" : "N/A"));

        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList(
                new PieChart.Data("Watched", watched),
                new PieChart.Data("Unwatched", unwatched)
        );
        statusPieChart.setData(pieData);
        statusPieChart.setTitle("Watch Status");
    }

    @FXML
    private void handleBack() {
        try {
            Main.switchScene("/com/movieapp/fxml/dashboard.fxml", "Movie Watchlist Manager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}