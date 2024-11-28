package com.example.client;

import com.example.client.modules.SessionBackup;
import com.example.client.modules.Structures;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {
    /** Classe controllore per gestire il processo di login */
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;

    public String user_mail;

    public void handleLogin() {
        /** Funzione che giudica l'input dell'utente e agisce di conseguenza */
        user_mail = emailField.getText();

        if (!Structures.isValidEmail(user_mail)) {
            errorLabel.setText("Invalid email format. Please try again.");
        } else {
            /** Creo un nuovo backup e entro nel client mail */
            InboxController inbox_controller = Structures.change_scene("Inbox.fxml", (Stage) errorLabel.getScene().getWindow(), getClass());
            SessionBackup backup = new SessionBackup(user_mail);
            inbox_controller.access_inbox(backup);
        }


    }
}