module com.example.client {
    requires com.example.shared;
    requires javafx.fxml;
    requires javafx.controls;
    requires json;


    opens com.example.client to javafx.fxml;
    exports com.example.client;
}