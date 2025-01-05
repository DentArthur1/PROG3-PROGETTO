module com.example.server {
    requires com.example.shared;
    requires javafx.fxml;
    requires javafx.controls;
    requires json;


    opens com.example.server to javafx.fxml;
    exports com.example.server;
}