module com.example.prog3vero {
    requires javafx.controls;
    requires javafx.fxml;


    opens esempio to javafx.fxml;
    exports esempio;
}