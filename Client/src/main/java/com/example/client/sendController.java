package com.example.client;

import com.example.client.modules.SessionBackup;
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
    @FXML
    private TextArea recipientsList; // Area per la lista dei destinatari

    public SessionBackup backup;


    // Aggiungi Destinatario
    @FXML
    protected void addRecipient() {
        String destinatario = toField.getText().trim();

        // Verifica se il campo del destinatario non è vuoto e se l'indirizzo è valido
        if (!destinatario.isEmpty() && verificaDestinatario(destinatario)) {
            // Aggiungi il destinatario alla lista visibile
            recipientsList.appendText(destinatario + "\n");
            // Cancella il campo per il prossimo inserimento
            toField.clear();
        } else {
            statusLabel.setText("Errore: Indirizzo email non valido.");
        }
    }

// pulsante "Invia"
        @FXML
        protected void sendEmail() {
            String oggetto = subjectField.getText().trim();
            String corpo = bodyArea.getText().trim();
            String destinatari = recipientsList.getText().trim(); // Legge tutti i destinatari

            // Controllo della correttezza degli input
            if (destinatari.isEmpty() || oggetto.isEmpty() || corpo.isEmpty()) {
                statusLabel.setText("Errore: Tutti i campi devono essere compilati.");
                return;
            }

            // Simula l'invio dell'email
            statusLabel.setText("Email inviata con successo!");

            // Pulisce i campi (opzionale)
            toField.clear();
            recipientsList.clear();
            subjectField.clear();
            bodyArea.clear();
        }

    // Metodo per verificare la sintassi di un indirizzo email
    private boolean verificaDestinatario(String destinatario) {
        String regexEmail = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return destinatario.matches(regexEmail);
    }

    //Funzione per tornare alla schermata precendente
    @FXML
    public void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Inbox.fxml"));
            Scene inboxScene = new Scene(loader.load());

            // Ottieni il controller associato alla nuova scena
            InboxController controller = loader.getController();

            // Verifica che il backup non sia null
            if (backup == null) {
                System.err.println("Errore: Il backup è null nel SendController.");
            } else {
                controller.access_session(backup);
            }

            // Cambia scena
            Stage stage = (Stage) statusLabel.getScene().getWindow();
            stage.setScene(inboxScene);
            stage.setTitle("Inbox");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
