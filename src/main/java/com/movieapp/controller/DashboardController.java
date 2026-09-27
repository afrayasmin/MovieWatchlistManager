package com.movieapp.controller;

import com.movieapp.Main;
import com.movieapp.dao.MovieDAO;
import com.movieapp.model.Movie;
import com.movieapp.ui.UiTheme;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class DashboardController {

    @FXML private BorderPane rootPane;
    @FXML private VBox headerBox;
    @FXML private Label titleLabel;
    @FXML private Label subtitleLabel;

    @FXML private VBox totalCard;
    @FXML private Label totalIcon;
    @FXML private Label totalMoviesLabel;
    @FXML private Label totalCaption;

    @FXML private VBox watchedCard;
    @FXML private Label watchedIcon;
    @FXML private Label watchedLabel;
    @FXML private Label watchedCaption;

    @FXML private VBox unwatchedCard;
    @FXML private Label unwatchedIcon;
    @FXML private Label unwatchedLabel;
    @FXML private Label unwatchedCaption;

    @FXML private VBox recentPanel;
    @FXML private Label recentSectionTitle;
    @FXML private ListView<String> recentMoviesList;

    @FXML private HBox buttonBar;
    @FXML private Button addMovieButton;
    @FXML private Button viewMoviesButton;
    @FXML private Button statisticsButton;

    private final MovieDAO movieDAO = new MovieDAO();

    @FXML
    public void initialize() {
        applyStyling();
        loadStats();
        loadRecentMovies();
    }

    private void applyStyling() {
        UiTheme.fillBackground(rootPane, UiTheme.BG_ROOT);

        UiTheme.fillGradientBackground(headerBox,
                UiTheme.horizontalGradient(UiTheme.BG_HEADER_1, UiTheme.BG_HEADER_2), 0);
        headerBox.setEffect(UiTheme.headerShadow());
        UiTheme.styleLabel(titleLabel, UiTheme.TEXT_PRIMARY, UiTheme.titleFont());
        UiTheme.styleLabel(subtitleLabel, UiTheme.TEXT_MUTED, UiTheme.subtitleFont());

        styleStatCard(totalCard, UiTheme.PURPLE_1, UiTheme.PURPLE_2, totalIcon, totalMoviesLabel, totalCaption);
        styleStatCard(watchedCard, UiTheme.GREEN_1, UiTheme.GREEN_2, watchedIcon, watchedLabel, watchedCaption);
        styleStatCard(unwatchedCard, UiTheme.ORANGE_1, UiTheme.ORANGE_2, unwatchedIcon, unwatchedLabel, unwatchedCaption);

        recentPanel.setPadding(new Insets(18));
        UiTheme.styleCardWithBorder(recentPanel, UiTheme.BG_CARD, UiTheme.BORDER_LIGHT, 12);
        recentPanel.setEffect(UiTheme.panelShadow());
        UiTheme.styleLabel(recentSectionTitle, UiTheme.TEXT_PRIMARY, UiTheme.sectionTitleFont());
        UiTheme.styleLabel(recentSectionTitle, UiTheme.TEXT_PRIMARY, UiTheme.sectionTitleFont());
        UiTheme.styleListViewCells(recentMoviesList);

        UiTheme.fillBackground(buttonBar, UiTheme.BG_HEADER_1);
        UiTheme.stylePrimaryButton(addMovieButton);
        UiTheme.stylePrimaryButton(viewMoviesButton);
        UiTheme.stylePrimaryButton(statisticsButton);
        addMovieButton.setPadding(new Insets(13, 26, 13, 26));
        viewMoviesButton.setPadding(new Insets(13, 26, 13, 26));
        statisticsButton.setPadding(new Insets(13, 26, 13, 26));
    }

    private void styleStatCard(VBox card, javafx.scene.paint.Color from, javafx.scene.paint.Color to,
                               Label icon, Label number, Label caption) {
        card.setPadding(new Insets(22, 32, 22, 32));
        card.setMinWidth(150);
        UiTheme.fillGradientBackground(card, UiTheme.diagonalGradient(from, to), 14);
        card.setEffect(UiTheme.cardShadow());
        UiTheme.styleLabel(icon, UiTheme.TEXT_WHITE, UiTheme.cardIconFont());
        UiTheme.styleLabel(number, UiTheme.TEXT_WHITE, UiTheme.cardNumberFont());
        UiTheme.styleLabel(caption, javafx.scene.paint.Color.rgb(255, 255, 255, 0.85), UiTheme.cardCaptionFont());
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
        List<Movie> movies = movieDAO.getAllMovies();
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