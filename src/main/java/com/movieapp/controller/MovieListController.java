package com.movieapp.controller;

import com.movieapp.Main;
import com.movieapp.dao.MovieDAO;
import com.movieapp.model.Movie;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MovieListController {

    @FXML private TableView<Movie> movieTable;
    @FXML private TableColumn<Movie, String> titleColumn;
    @FXML private TableColumn<Movie, String> genreColumn;
    @FXML private TableColumn<Movie, Integer> yearColumn;
    @FXML private TableColumn<Movie, Double> ratingColumn;
    @FXML private TableColumn<Movie, String> myRatingColumn;
    @FXML private TableColumn<Movie, String> statusColumn;
    @FXML private TableColumn<Movie, String> favoriteColumn;

    @FXML private TextField searchField;
    @FXML private ComboBox<String> genreFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private CheckBox favoritesOnlyCheck;

    private final MovieDAO movieDAO = new MovieDAO();

    @FXML
    public void initialize() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        myRatingColumn.setCellValueFactory(new PropertyValueFactory<>("myRatingDisplay"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        favoriteColumn.setCellValueFactory(new PropertyValueFactory<>("favoriteDisplay"));

        genreFilterCombo.setItems(FXCollections.observableArrayList(
                "All", "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Animation"
        ));
        genreFilterCombo.setValue("All");

        statusFilterCombo.setItems(FXCollections.observableArrayList("All", "Watched", "Unwatched"));
        statusFilterCombo.setValue("All");

        loadAllMovies();
    }

    private void loadAllMovies() {
        List<Movie> movies = movieDAO.getAllMovies();
        movieTable.setItems(FXCollections.observableArrayList(movies));
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMovies();
            return;
        }
        ObservableList<Movie> results = FXCollections.observableArrayList(movieDAO.searchMovies(keyword));
        movieTable.setItems(results);
    }

    @FXML
    private void handleFilter() {
        String genre = genreFilterCombo.getValue();
        String status = statusFilterCombo.getValue();
        boolean favoritesOnly = favoritesOnlyCheck.isSelected();
        ObservableList<Movie> results = FXCollections.observableArrayList(
                movieDAO.filterMovies(genre, status, favoritesOnly));
        movieTable.setItems(results);
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        genreFilterCombo.setValue("All");
        statusFilterCombo.setValue("All");
        favoritesOnlyCheck.setSelected(false);
        loadAllMovies();
    }

    @FXML
    private void handleAdd() {
        try {
            Main.switchScene("/com/movieapp/fxml/add-movie.fxml", "Add New Movie");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleEdit() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a movie to edit.");
            return;
        }
        try {
            Object controller = Main.switchScene("/com/movieapp/fxml/add-movie.fxml", "Edit Movie");
            if (controller instanceof AddMovieController editController) {
                editController.setEditMovie(selected);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDelete() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a movie to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText(null);
        confirm.setContentText("Are you sure you want to delete \"" + selected.getTitle() + "\"?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = movieDAO.deleteMovie(selected.getId());
                if (success) {
                    loadAllMovies();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Failed to delete movie.");
                }
            }
        });
    }

    @FXML
    private void handleToggleStatus() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a movie.");
            return;
        }

        String newStatus = selected.getStatus().equals("Watched") ? "Unwatched" : "Watched";
        boolean success = movieDAO.toggleStatus(selected.getId(), newStatus);
        if (success) {
            loadAllMovies();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to update status.");
        }
    }

    @FXML
    private void handleToggleFavorite() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a movie.");
            return;
        }

        boolean success = movieDAO.toggleFavorite(selected.getId(), !selected.isFavorite());
        if (success) {
            loadAllMovies();
        } else {
            showAlert(Alert.AlertType.ERROR, "Failed to update favorite.");
        }
    }

    @FXML
    private void handleBack() {
        try {
            Main.switchScene("/com/movieapp/fxml/dashboard.fxml", "Movie Watchlist Manager");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}