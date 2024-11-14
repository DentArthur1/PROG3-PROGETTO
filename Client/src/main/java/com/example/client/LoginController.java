package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";


    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public void handleLogin() {
        String email = emailField.getText();

        // Controlla se l'email è conforme alla regex
        if (!isValidEmail(email)) {
            errorLabel.setText("Invalid email format. Please try again.");
        } else {

            try {
                // Carica il file FXML per la Home
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Inbox.fxml"));
                Scene homeScene = new Scene(loader.load());

                // Ottieni il Stage corrente (finestra)
                Stage stage = (Stage) errorLabel.getScene().getWindow();

                // Imposta la nuova scena
                stage.setScene(homeScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
        }
    }


}
}
