package com.example.client;

import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
            InboxController inbox_controller = Structures.change_scene((Stage) errorLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Inbox.fxml")));
            SessionBackup backup = new SessionBackup(user_mail);
            inbox_controller.access_inbox(backup);
        }


    }
}