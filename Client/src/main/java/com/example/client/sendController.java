package com.example.client;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class sendController {

    @FXML
    private TextField toField; // destinatari
    @FXML
    private TextField subjectField; // oggetto
    @FXML
    private TextArea bodyArea; // messaggio
    @FXML
    private Label statusLabel; // messaggi di stato

    // click del pulsante "Invia"
    @FXML
    protected void sendEmail() {
        String destinatari = toField.getText().trim();
        String oggetto = subjectField.getText().trim();
        String corpo = bodyArea.getText().trim();


        // Invio dell'email
        statusLabel.setText("Email inviata con successo!");
    }
}
