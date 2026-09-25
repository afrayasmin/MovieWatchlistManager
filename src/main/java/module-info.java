module com.movieapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.net.http;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;

    opens com.movieapp to javafx.fxml;
    opens com.movieapp.controller to javafx.fxml;
    opens com.movieapp.model to javafx.fxml, javafx.base, com.fasterxml.jackson.databind;

    exports com.movieapp;
}