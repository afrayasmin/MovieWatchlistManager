package com.movieapp;

import com.movieapp.database.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        DatabaseConnection.initializeDatabase();
        stage = primaryStage;

        switchScene("/com/movieapp/fxml/dashboard.fxml", "Movie Watchlist Manager");
        primaryStage.show();
    }

    // Now returns the controller so callers can pass data into the new screen
    public static Object switchScene(String fxmlPath, String title) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
        Scene scene = new Scene(loader.load(), 1000, 650);
        stage.setTitle(title);
        stage.setScene(scene);
        return loader.getController();
    }

    public static void main(String[] args) {
        launch(args);
    }
}