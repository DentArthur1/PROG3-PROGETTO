module com.example.shared {
    requires javafx.controls;
    requires javafx.fxml;
    requires json;


    opens com.example.shared to javafx.fxml;
    exports com.example.shared;
}