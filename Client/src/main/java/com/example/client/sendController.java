package com.example.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

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

    //Funzione per tornare alla schermata precendente
    @FXML
    public void goBack() {

        try {
            // Carica il file FXML per la Home (o per una scena precedente)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Inbox.fxml")); // Sostituisci con il percorso giusto
            // Carica la nuova scena
            Scene inboxScene = new Scene(loader.load());

            // Ottieni il Stage corrente (finestra)
            Stage stage = (Stage) statusLabel.getScene().getWindow();

            // Imposta la nuova scena (sostituisce la scena corrente con la nuova)
            stage.setScene(inboxScene);
            stage.show(); // Mostra la nuova scena
        } catch (IOException e) {
            e.printStackTrace();
    }
}}
