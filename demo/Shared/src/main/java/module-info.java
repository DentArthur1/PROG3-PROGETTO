module com.example.shared {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.example.shared to javafx.fxml, com.fasterxml.jackson.databind;
    exports com.example.shared;
}