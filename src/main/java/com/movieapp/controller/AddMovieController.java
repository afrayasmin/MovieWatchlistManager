package com.movieapp.controller;

import com.movieapp.dao.MovieDAO;
import com.movieapp.database.DatabaseActivityMonitor;
import com.movieapp.model.Movie;
import com.movieapp.model.OmdbMovieResult;
import com.movieapp.service.OmdbService;
import com.movieapp.ui.UiTheme;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddMovieController {

    private static final int SIMULATED_DB_DELAY_MS = 0;

    @FXML private BorderPane rootPane;
    @FXML private VBox headerBox;
    @FXML private Label titleLabel;

    @FXML private Label titleFieldLabel;
    @FXML private TextField titleField;
    @FXML private Button searchOnlineButton;
    @FXML private Label genreLabel;
    @FXML private ComboBox<String> genreCombo;
    @FXML private Label yearLabel;
    @FXML private TextField yearField;
    @FXML private Label ratingLabel;
    @FXML private TextField ratingField;
    @FXML private Label myRatingLabel;
    @FXML private TextField myRatingField;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label notesLabel;
    @FXML private TextArea notesArea;
    @FXML private CheckBox favoriteCheckBox;
    @FXML private Label errorLabel;

    @FXML private HBox buttonBar;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private final MovieDAO movieDAO = new MovieDAO();
    private final OmdbService omdbService = new OmdbService();
    private Movie editingMovie = null;

    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread t = new Thread(runnable, "add-movie-db-worker");
        t.setDaemon(true);
        return t;
    });

    private final ExecutorService networkExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread t = new Thread(runnable, "omdb-network-worker");
        t.setDaemon(true);
        return t;
    });

    @FXML
    public void initialize() {
        applyStyling();

        genreCombo.setItems(FXCollections.observableArrayList(
                "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Animation", "Romantic"
        ));
        statusCombo.setItems(FXCollections.observableArrayList("Watched", "Unwatched"));
        statusCombo.setValue("Unwatched");

        statusCombo.valueProperty().addListener((obs, oldVal, newVal) -> updateMyRatingFieldState(newVal));
        updateMyRatingFieldState(statusCombo.getValue());
    }

    private void applyStyling() {
        UiTheme.fillBackground(rootPane, UiTheme.BG_ROOT);

        UiTheme.fillGradientBackground(headerBox,
                UiTheme.horizontalGradient(UiTheme.BG_HEADER_1, UiTheme.BG_HEADER_2), 0);
        headerBox.setEffect(UiTheme.headerShadow());
        UiTheme.styleLabel(titleLabel, UiTheme.TEXT_PRIMARY, UiTheme.titleFont());

        for (Label l : new Label[]{titleFieldLabel, genreLabel, yearLabel, ratingLabel,
                myRatingLabel, statusLabel, notesLabel}) {
            UiTheme.styleLabel(l, UiTheme.TEXT_SECONDARY, UiTheme.bodyFont());
        }

        UiTheme.styleTextInput(titleField, UiTheme.TEXT_SECONDARY);
        UiTheme.styleTextInput(yearField, UiTheme.TEXT_SECONDARY);
        UiTheme.styleTextInput(ratingField, UiTheme.TEXT_SECONDARY);
        UiTheme.styleTextInput(myRatingField, UiTheme.TEXT_SECONDARY);
        UiTheme.styleTextInput(notesArea, UiTheme.TEXT_SECONDARY);

        UiTheme.styleComboBox(genreCombo);
        UiTheme.styleComboBox(statusCombo);
        UiTheme.styleComboBoxText(genreCombo);
        UiTheme.styleComboBoxText(statusCombo);
        UiTheme.styleComboBoxPopup(genreCombo);
        UiTheme.styleComboBoxPopup(statusCombo);

        UiTheme.styleCheckBoxBox(favoriteCheckBox);

        UiTheme.styleLabel(errorLabel, UiTheme.RED_1, javafx.scene.text.Font.font("Segoe UI",
                javafx.scene.text.FontWeight.BOLD, 12));

        UiTheme.stylePrimaryButton(searchOnlineButton);
        UiTheme.stylePrimaryButton(saveButton);
        UiTheme.styleSecondaryButton(cancelButton);

        UiTheme.fillBackground(buttonBar, UiTheme.BG_HEADER_1);
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

    // ---------- Search Online (OMDb / Jackson) ----------

    @FXML
    private void handleSearchOnline() {
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            errorLabel.setText("Enter a movie title first, then search online.");
            return;
        }

        errorLabel.setText("Searching online...");
        searchOnlineButton.setDisable(true);
        saveButton.setDisable(true);

        Task<OmdbMovieResult> task = new Task<>() {
            @Override
            protected OmdbMovieResult call() throws IOException {
                return omdbService.searchByTitle(title);
            }
        };

        task.setOnSucceeded(event -> {
            searchOnlineButton.setDisable(false);
            saveButton.setDisable(false);

            OmdbMovieResult result = task.getValue();
            if (result == null || !result.isSuccess()) {
                String msg = (result != null && result.getError() != null)
                        ? result.getError() : "Movie not found online.";
                errorLabel.setText(msg);
                return;
            }

            applySearchResult(result);
            errorLabel.setText("Filled from online data — review before saving.");
        });

        task.setOnFailed(event -> {
            searchOnlineButton.setDisable(false);
            saveButton.setDisable(false);
            errorLabel.setText("Could not reach OMDb. Check your internet connection.");
            Throwable ex = task.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
        });

        networkExecutor.submit(task);
    }

    private void applySearchResult(OmdbMovieResult result) {
        if (result.getTitle() != null && !result.getTitle().isBlank()) {
            titleField.setText(result.getTitle());
        }

        String year = extractFirstYear(result.getYear());
        if (!year.isEmpty()) {
            yearField.setText(year);
        }

        if (result.getImdbRating() != null && !result.getImdbRating().equalsIgnoreCase("N/A")) {
            try {
                double parsedRating = Double.parseDouble(result.getImdbRating());
                ratingField.setText(String.valueOf(parsedRating));
            } catch (NumberFormatException ignored) {
            }
        }

        applyGenreFromOmdb(result.getGenre());

        if (notesArea.getText().isBlank() && result.getPlot() != null
                && !result.getPlot().equalsIgnoreCase("N/A")) {
            notesArea.setText(result.getPlot());
        }
    }

    private String extractFirstYear(String omdbYear) {
        if (omdbYear == null) {
            return "";
        }
        Matcher matcher = Pattern.compile("\\d{4}").matcher(omdbYear);
        return matcher.find() ? matcher.group() : "";
    }

    private void applyGenreFromOmdb(String omdbGenre) {
        if (omdbGenre == null || omdbGenre.isBlank()) {
            return;
        }

        for (String part : omdbGenre.split(",")) {
            String candidate = part.trim();
            if (candidate.equalsIgnoreCase("Romance")) {
                candidate = "Romantic";
            }

            for (String appGenre : genreCombo.getItems()) {
                if (appGenre.equalsIgnoreCase(candidate)) {
                    genreCombo.setValue(appGenre);
                    return;
                }
            }
        }
    }

    // ---------- Save ----------

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

        double finalRating = myRating;
        int finalYear = year;
        double finalImdbRating = rating;

        Task<Boolean> saveTask = new Task<>() {
            @Override
            protected Boolean call() throws InterruptedException {
                DatabaseActivityMonitor.operationStarted();
                try {
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
        shutdownExecutors();
        goToMovieList();
    }

    private void goToMovieList() {
        try {
            com.movieapp.Main.switchScene("/com/movieapp/fxml/movie-list.fxml", "Movie List");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shutdownExecutors() {
        dbExecutor.shutdown();
        networkExecutor.shutdown();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}