module com.movieapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.movieapp to javafx.fxml;
    opens com.movieapp.controller to javafx.fxml;
    opens com.movieapp.model to javafx.fxml, javafx.base;

    exports com.movieapp;
}