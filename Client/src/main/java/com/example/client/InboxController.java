package com.example.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.IOException;

public class InboxController {

    @FXML
    private Label connectionStatus;

    @FXML
    protected void handleLogout() {
        try {
            // Carica il file FXML per la scena "Login.fxml"
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            // Ottieni lo Stage corrente (la finestra)
            Stage currentStage = (Stage) connectionStatus.getScene().getWindow();

            // Sostituisci la scena attuale con quella di login
            currentStage.setScene(loginScene);
            currentStage.setTitle("Login");  // Imposta il titolo della finestra su "Login"
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCompose() {
        // Logica per comporre una nuova email
        try {
            // Carica il file FXML per la scena "Send.fxml"
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Send.fxml"));
            Scene sendScene = new Scene(fxmlLoader.load());

            // Ottieni lo Stage corrente (la finestra)
            Stage currentStage = (Stage) connectionStatus.getScene().getWindow();

            // Sostituisci la scena attuale con quella nuova (Sen.fxml)
            currentStage.setScene(sendScene);
            currentStage.setTitle("Scrivi");  // Aggiorna il titolo della finestra se necessario
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleDelete() {
        // Logic for deleting an email
    }

    @FXML
    protected void handleEmailClick() {
        // Logic for handling email click events
    }
}
