module com.example.server {
    requires com.example.shared;
    requires javafx.fxml;
    requires javafx.controls;
    requires com.fasterxml.jackson.databind;


    opens com.example.server to javafx.fxml;
    exports com.example.server;
}