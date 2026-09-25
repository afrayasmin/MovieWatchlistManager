package com.movieapp.controller;

import com.movieapp.Main;
import com.movieapp.dao.MovieDAO;
import com.movieapp.database.DatabaseActivityMonitor;
import com.movieapp.model.Movie;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    @FXML private Label statusLabel;
    @FXML private VBox loadingOverlay;

    private final MovieDAO movieDAO = new MovieDAO();

    // A small thread pool that runs every database operation (reads AND writes)
    // off the JavaFX Application Thread, so nothing about talking to SQLite
    // ever freezes the UI. A single named worker thread is plenty for this
    // app's scale — operations are queued and run one after another, which
    // also avoids concurrent writes to the same SQLite file.
    private final ExecutorService dbExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread t = new Thread(runnable, "db-worker");
        t.setDaemon(true);
        return t;
    });

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
                "All", "Action", "Comedy", "Drama", "Horror", "Sci-Fi", "Thriller", "Animation", "Romantic"
        ));
        genreFilterCombo.setValue("All");

        statusFilterCombo.setItems(FXCollections.observableArrayList("All", "Watched", "Unwatched"));
        statusFilterCombo.setValue("All");

        loadAllMovies();
    }

    // ---------- Loading overlay ----------

    private void showLoading() {
        loadingOverlay.setVisible(true);
        loadingOverlay.setManaged(true);
    }

    private void hideLoading() {
        loadingOverlay.setVisible(false);
        loadingOverlay.setManaged(false);
    }

    // ---------- READ operations ----------

    // Submits a database query as a background Task, then safely applies the
    // result back on the JavaFX Application Thread once it completes.
    // Task's onSucceeded/onFailed callbacks are guaranteed to run on the UI
    // thread, which is what makes this safe without manual Platform.runLater.
    private void runQueryInBackground(Task<List<Movie>> task) {
        movieTable.setDisable(true);
        statusLabel.setText("Loading...");
        showLoading();

        task.setOnSucceeded(event -> {
            List<Movie> result = task.getValue();
            movieTable.setItems(FXCollections.observableArrayList(result));
            movieTable.setDisable(false);
            statusLabel.setText(result.size() + " movie(s)");
            hideLoading();
        });

        task.setOnFailed(event -> {
            movieTable.setDisable(false);
            statusLabel.setText("Failed to load movies.");
            hideLoading();
            Throwable ex = task.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
        });

        dbExecutor.submit(task);
    }

    private void loadAllMovies() {
        Task<List<Movie>> task = new Task<>() {
            @Override
            protected List<Movie> call() {
                DatabaseActivityMonitor.operationStarted();
                try {
                    return movieDAO.getAllMovies();
                } finally {
                    DatabaseActivityMonitor.operationFinished();
                }
            }
        };
        runQueryInBackground(task);
    }

    @FXML
    private void handleSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadAllMovies();
            return;
        }

        Task<List<Movie>> task = new Task<>() {
            @Override
            protected List<Movie> call() {
                DatabaseActivityMonitor.operationStarted();
                try {
                    return movieDAO.searchMovies(keyword);
                } finally {
                    DatabaseActivityMonitor.operationFinished();
                }
            }
        };
        runQueryInBackground(task);
    }

    @FXML
    private void handleFilter() {
        String genre = genreFilterCombo.getValue();
        String status = statusFilterCombo.getValue();
        boolean favoritesOnly = favoritesOnlyCheck.isSelected();

        Task<List<Movie>> task = new Task<>() {
            @Override
            protected List<Movie> call() {
                DatabaseActivityMonitor.operationStarted();
                try {
                    return movieDAO.filterMovies(genre, status, favoritesOnly);
                } finally {
                    DatabaseActivityMonitor.operationFinished();
                }
            }
        };
        runQueryInBackground(task);
    }

    @FXML
    private void handleReset() {
        searchField.clear();
        genreFilterCombo.setValue("All");
        statusFilterCombo.setValue("All");
        favoritesOnlyCheck.setSelected(false);
        loadAllMovies();
    }

    // ---------- WRITE operations ----------

    // Submits a database write (delete/toggle/etc.) as a background Task.
    // On success, reloads the table (itself threaded) so the UI reflects the
    // change. On failure, shows the given error message on the UI thread.
    private void runWriteInBackground(Task<Boolean> task, String failureMessage) {
        statusLabel.setText("Saving...");
        showLoading();

        task.setOnSucceeded(event -> {
            boolean success = task.getValue();
            if (success) {
                loadAllMovies();
            } else {
                statusLabel.setText("");
                hideLoading();
                showAlert(Alert.AlertType.ERROR, failureMessage);
            }
        });

        task.setOnFailed(event -> {
            statusLabel.setText("");
            hideLoading();
            showAlert(Alert.AlertType.ERROR, failureMessage);
            Throwable ex = task.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
        });

        dbExecutor.submit(task);
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
                Task<Boolean> task = new Task<>() {
                    @Override
                    protected Boolean call() {
                        DatabaseActivityMonitor.operationStarted();
                        try {
                            return movieDAO.deleteMovie(selected.getId());
                        } finally {
                            DatabaseActivityMonitor.operationFinished();
                        }
                    }
                };
                runWriteInBackground(task, "Failed to delete movie.");
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

        boolean switchingToWatched = "Unwatched".equals(selected.getStatus());

        if (switchingToWatched) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Mark as Watched");
            dialog.setHeaderText("Rate \"" + selected.getTitle() + "\"");
            dialog.setContentText("My Rating (0-10):");

            Optional<String> result = dialog.showAndWait();
            if (result.isEmpty()) {
                return;
            }

            double myRating;
            try {
                myRating = Double.parseDouble(result.get().trim());
                if (myRating < 0 || myRating > 10) {
                    showAlert(Alert.AlertType.ERROR, "Rating must be between 0 and 10.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Rating must be a number.");
                return;
            }

            double finalRating = myRating;
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    DatabaseActivityMonitor.operationStarted();
                    try {
                        return movieDAO.markWatchedWithRating(selected.getId(), finalRating);
                    } finally {
                        DatabaseActivityMonitor.operationFinished();
                    }
                }
            };
            runWriteInBackground(task, "Failed to update status.");

        } else {
            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    DatabaseActivityMonitor.operationStarted();
                    try {
                        return movieDAO.toggleStatus(selected.getId(), "Unwatched");
                    } finally {
                        DatabaseActivityMonitor.operationFinished();
                    }
                }
            };
            runWriteInBackground(task, "Failed to update status.");
        }
    }

    @FXML
    private void handleToggleFavorite() {
        Movie selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a movie.");
            return;
        }

        Task<Boolean> task = new Task<>() {
            @Override
            protected Boolean call() {
                DatabaseActivityMonitor.operationStarted();
                try {
                    return movieDAO.toggleFavorite(selected.getId(), !selected.isFavorite());
                } finally {
                    DatabaseActivityMonitor.operationFinished();
                }
            }
        };
        runWriteInBackground(task, "Failed to update favorite.");
    }

    @FXML
    private void handleBack() {
        // Shut the thread pool down cleanly when leaving this screen —
        // a new one is created if the user navigates back here again.
        dbExecutor.shutdown();
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