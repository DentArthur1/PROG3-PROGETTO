package com.example.client;

import com.example.client.modules.Mail;
import com.example.client.modules.SessionBackup;
import javafx.collections.ObservableList;
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

    public String user_mail;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";


    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public void handleLogin() {
        user_mail = emailField.getText();

        // Controlla se l'email è conforme alla regex
        if (!isValidEmail(user_mail)) {
            errorLabel.setText("Invalid email format. Please try again.");
        } else {

            try {
                // Carica il file FXML per la Home
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Inbox.fxml"));
                Scene homeScene = new Scene(loader.load());

                // Ottieni il Stage corrente (finestra)
                Stage stage = (Stage) errorLabel.getScene().getWindow();
                // Passare i dati al nuovo controller
                InboxController controller = loader.getController();

                //Begin session
                SessionBackup sessionBackup = new SessionBackup(user_mail);
                controller.access_session(sessionBackup);

                // Imposta la nuova scena
                stage.setScene(homeScene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}