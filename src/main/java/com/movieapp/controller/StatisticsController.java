package com.movieapp.controller;

import com.movieapp.Main;
import com.movieapp.dao.MovieDAO;
import com.movieapp.model.Movie;
import com.movieapp.ui.UiTheme;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsController {

    @FXML private BorderPane rootPane;
    @FXML private VBox headerBox;
    @FXML private Label titleLabel;

    @FXML private VBox statsPanel;
    @FXML private Label totalLabel;
    @FXML private Label watchedLabel;
    @FXML private Label unwatchedLabel;
    @FXML private Label avgRatingLabel;
    @FXML private Label avgMyRatingLabel;
    @FXML private Label commonGenreLabel;
    @FXML private Label highestRatedLabel;
    @FXML private PieChart statusPieChart;
    @FXML private PieChart genrePieChart;

    @FXML private HBox buttonBar;
    @FXML private Button backButton;

    private final MovieDAO movieDAO = new MovieDAO();

    @FXML
    public void initialize() {
        applyStyling();

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

        ObservableList<PieChart.Data> genreData = genreCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        // Animation disabled BEFORE data is applied, so PieChart doesn't
        // recreate/animate its internal label nodes while we're styling.
        statusPieChart.setAnimated(false);
        genrePieChart.setAnimated(false);

        statusPieChart.setData(pieData);
        statusPieChart.setTitle("Watch Status");

        genrePieChart.setData(genreData);
        genrePieChart.setTitle("Genre Breakdown");

        // Listens for the chart's internal nodes appearing (slices, labels,
        // legend, title) and re-applies dark styling every time, rather
        // than guessing when JavaFX has finished building them.
        UiTheme.bindDarkChartStyling(statusPieChart, UiTheme.PURPLE_1, UiTheme.ORANGE_1);
        UiTheme.bindDarkChartStyling(genrePieChart, UiTheme.PIE_PALETTE);
    }

    private void applyStyling() {
        UiTheme.fillBackground(rootPane, UiTheme.BG_ROOT);

        UiTheme.fillGradientBackground(headerBox,
                UiTheme.horizontalGradient(UiTheme.BG_HEADER_1, UiTheme.BG_HEADER_2), 0);
        headerBox.setEffect(UiTheme.headerShadow());
        UiTheme.styleLabel(titleLabel, UiTheme.TEXT_PRIMARY, UiTheme.titleFont());

        UiTheme.styleCardWithBorder(statsPanel, UiTheme.BG_CARD, UiTheme.BORDER_LIGHT, 12);
        statsPanel.setEffect(UiTheme.panelShadow());
        for (Label l : new Label[]{totalLabel, watchedLabel, unwatchedLabel, avgRatingLabel,
                avgMyRatingLabel, commonGenreLabel, highestRatedLabel}) {
            UiTheme.styleLabel(l, UiTheme.TEXT_PRIMARY, UiTheme.statLabelFont());
        }

        UiTheme.fillBackground(statusPieChart, UiTheme.BG_ROOT);
        UiTheme.fillBackground(genrePieChart, UiTheme.BG_ROOT);

        UiTheme.fillBackground(buttonBar, UiTheme.BG_HEADER_1);
        UiTheme.stylePrimaryButton(backButton);
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