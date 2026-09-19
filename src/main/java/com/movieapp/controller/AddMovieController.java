package com.movieapp.controller;

import com.movieapp.dao.MovieDAO;
import com.movieapp.model.Movie;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AddMovieController {

    @FXML private TextField titleField;
    @FXML private ComboBox<String> genreCombo;
    @FXML private TextField yearField;
    @FXML private TextField ratingField;
    @FXML private TextField myRatingField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea notesArea;
    @FXML private CheckBox favoriteCheckBox;
    @FXML private Label errorLabel;

    private final MovieDAO movieDAO = new MovieDAO();
    private Movie editingMovie = null;

    @FXML
    public void initialize() {
        genreCombo.setItems(FXCollections.observableArrayList(
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Animation"
        ));
        statusCombo.setItems(FXCollections.observableArrayList("Watched", "Unwatched"));
        statusCombo.setValue("Unwatched");

        // Personal rating is only relevant once a movie is marked Watched
        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMyRatingFieldState(newVal));
        updateMyRatingFieldState(statusCombo.getValue());
    }

    private void updateMyRatingFieldState(String status) {
        boolean watched = "Watched".equals(status);
        myRatingField.setDisable(!watched);
        if (!watched) {
            myRatingField.clear();
            myRatingField.setPromptText("Not watched yet");
        } else {
            myRatingField.setPromptText("Optional");
        }
    }

    public void setEditMovie(Movie movie) {
        this.editingMovie = movie;

        titleField.setText(movie.getTitle());
        genreCombo.setValue(movie.getGenre());
        yearField.setText(String.valueOf(movie.getReleaseYear()));
        ratingField.setText(String.valueOf(movie.getRating()));

        // Setting statusCombo triggers the listener above, which enables/disables
        // and clears myRatingField as needed — so set status BEFORE the rating text.
        statusCombo.setValue(movie.getStatus());
        if ("Watched".equals(movie.getStatus())) {
            myRatingField.setText(movie.getMyRating() > 0 ? String.valueOf(movie.getMyRating()) : "");
        }

        notesArea.setText(movie.getNotes());
        favoriteCheckBox.setSelected(movie.isFavorite());
    }

    @FXML
    private void handleSave() {
        errorLabel.setText("");

        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            errorLabel.setText("Please enter the movie title.");
            return;
        }

        String genre = genreCombo.getValue();
        if (genre == null) {
            errorLabel.setText("Please select a genre.");
            return;
        }

        int year;
        try {
            year = Integer.parseInt(yearField.getText().trim());
            if (year < 1888 || year > 2026) {
                errorLabel.setText("Release year must be between 1888 and 2026.");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("Release year must be a number.");
            return;
        }

        double rating;
        try {
            rating = Double.parseDouble(ratingField.getText().trim());
            if (rating < 0 || rating > 10) {
                errorLabel.setText("IMDb rating must be between 0 and 10.");
                return;
            }
        } catch (NumberFormatException e) {
            errorLabel.setText("IMDb rating must be a number.");
            return;
        }

        String status = statusCombo.getValue();
        if (status == null) {
            errorLabel.setText("Please select a status.");
            return;
        }

        // My Rating is only allowed when the movie is Watched.
        // Unwatched movies always save with myRating = 0 (treated as "no rating" by the model).
        double myRating = 0;
        if ("Watched".equals(status)) {
            String myRatingText = myRatingField.getText().trim();
            if (!myRatingText.isEmpty()) {
                try {
                    myRating = Double.parseDouble(myRatingText);
                    if (myRating < 0 || myRating > 10) {
                        errorLabel.setText("My rating must be between 0 and 10.");
                        return;
                    }
                } catch (NumberFormatException e) {
                    errorLabel.setText("My rating must be a number.");
                    return;
                }
            }
        }

        String notes = notesArea.getText().trim();
        boolean favorite = favoriteCheckBox.isSelected();

        boolean success;

        if (editingMovie == null) {
            String dateAdded = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            Movie movie = new Movie(title, genre, year, rating, myRating, dateAdded, status, notes, favorite);
            success = movieDAO.addMovie(movie);
        } else {
            editingMovie.setTitle(title);
            editingMovie.setGenre(genre);
            editingMovie.setReleaseYear(year);
            editingMovie.setRating(rating);
            editingMovie.setMyRating(myRating);
            editingMovie.setStatus(status);
            editingMovie.setNotes(notes);
            editingMovie.setFavorite(favorite);
            success = movieDAO.updateMovie(editingMovie);
        }

        if (success) {
            showAlert(Alert.AlertType.INFORMATION,
                    editingMovie == null ? "Movie added successfully!" : "Movie updated successfully!");
            goToMovieList();
        } else {
            errorLabel.setText("Something went wrong while saving the movie.");
        }
    }

    @FXML
    private void handleCancel() {
        goToMovieList();
    }

    private void goToMovieList() {
        try {
            com.movieapp.Main.switchScene("/com/movieapp/fxml/movie-list.fxml", "Movie List");
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