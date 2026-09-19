package com.movieapp.controller;

import com.movieapp.dao.MovieDAO;
import com.movieapp.database.DatabaseActivityMonitor;
import com.movieapp.model.Movie;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddMovieController {

    // Set to 0 before final submission if you don't want the artificial delay
    // in the graded/production version. Kept as a named constant so it's easy
    // to find and change in one place.
    private static final int SIMULATED_DB_DELAY_MS = 1500;

    @FXML private TextField titleField;
    @FXML private ComboBox<String> genreCombo;
    @FXML private TextField yearField;
    @FXML private TextField ratingField;
    @FXML private TextField myRatingField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private TextArea notesArea;
    @FXML private CheckBox favoriteCheckBox;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final MovieDAO movieDAO = new MovieDAO();
    private Movie editingMovie = null;

    // Same pattern as MovieListController: DB writes run on a background
    // thread so the UI never freezes while saving.
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread t = new Thread(runnable, "add-movie-db-worker");
        t.setDaemon(true);
        return t;
    });

    @FXML
    public void initialize() {
        genreCombo.setItems(FXCollections.observableArrayList(
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Animation", "Romantic"
        ));
        statusCombo.setItems(FXCollections.observableArrayList("Watched", "Unwatched"));
        statusCombo.setValue("Unwatched");

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

        // All validation passed on the UI thread (fast, doesn't touch the DB).
        // The actual database write now runs on a background thread.
        double finalRating = myRating;
        int finalYear = year;
        double finalImdbRating = rating;

        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() throws InterruptedException {
                DatabaseActivityMonitor.operationStarted();
                try {
                    // Simulated delay so background threading is visibly
                    // demonstrable — SQLite writes are normally too fast to see.
                    if (SIMULATED_DB_DELAY_MS > 0) {
                        Thread.sleep(SIMULATED_DB_DELAY_MS);
                    }

                    if (editingMovie == null) {
                        String dateAdded = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                        Movie movie = new Movie(title, genre, finalYear, finalImdbRating, finalRating,
                                dateAdded, status, notes, favorite);
                        return movieDAO.addMovie(movie);
                    } else {
                        editingMovie.setTitle(title);
                        editingMovie.setGenre(genre);
                        editingMovie.setReleaseYear(finalYear);
                        editingMovie.setRating(finalImdbRating);
                        editingMovie.setMyRating(finalRating);
                        editingMovie.setStatus(status);
                        editingMovie.setNotes(notes);
                        editingMovie.setFavorite(favorite);
                        return movieDAO.updateMovie(editingMovie);
                    }
                } finally {
                    DatabaseActivityMonitor.operationFinished();
                }
            }
        };

        saveButton.setDisable(true);
        errorLabel.setText("Saving...");

        saveTask.setOnSucceeded(event -> {
            saveButton.setDisable(false);
            boolean success = saveTask.getValue();
            if (success) {
                showAlert(Alert.AlertType.INFORMATION,
                        editingMovie == null ? "Movie added successfully!" : "Movie updated successfully!");
                goToMovieList();
            } else {
                errorLabel.setText("Something went wrong while saving the movie.");
            }
        });

        saveTask.setOnFailed(event -> {
            saveButton.setDisable(false);
            errorLabel.setText("Something went wrong while saving the movie.");
            Throwable ex = saveTask.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
        });

        dbExecutor.submit(saveTask);
    }

    @FXML
    private void handleCancel() {
        dbExecutor.shutdown();
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