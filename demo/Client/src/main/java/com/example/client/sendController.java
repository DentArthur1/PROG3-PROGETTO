package com.example.client;

import com.example.shared.Mail;
import com.example.shared.Request;
import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
//import java.io.PrintWriter;
import java.net.Socket;
import java.io.*;
import java.time.LocalDateTime;

public class sendController {
    /** Classe controllore per gestire l'operazione di invio messaggi */

    @FXML
    private TextField toField; // destinatari
    @FXML
    private TextField subjectField; // oggetto
    @FXML
    private TextArea bodyArea; // messaggio
    @FXML
    private Label successLabel; // messaggi di stato
    @FXML
    private Label errorLabel; // etichetta di errore
    @FXML
    private TextArea receiversList; // Area per la lista dei destinatari

    public SessionBackup backup;

    @FXML
    protected void addReceiver() {
        String destinatario = toField.getText().trim();

        /** Verifica se il campo del destinatario non è vuoto e se l'indirizzo è valido */
        if (!destinatario.isEmpty() && Structures.isValidEmail(destinatario)) {
            // Aggiungi il destinatario alla lista visibile
            receiversList.appendText(destinatario + "\n");
            // Cancella il campo per il prossimo inserimento
            toField.clear();
        } else {
            errorLabel.setText("Errore: Indirizzo email non valido.");
            successLabel.setText(""); // Cancella eventuali successi precedenti
        }
    }

    @FXML
    protected void sendEmail() {
        String oggetto = subjectField.getText().trim();
        String corpo = bodyArea.getText().trim();
        String[] destinatari = receiversList.getText().trim().split(","); // Legge tutti i destinatari
        LocalDateTime date = LocalDateTime.now();
        Mail new_mail = new Mail("id", backup.getUserEmailBackup(), oggetto, corpo, destinatari, date);

        /** Controllo della correttezza degli input */
        if (destinatari.length == 0 || oggetto.isEmpty() || corpo.isEmpty()) {
            errorLabel.setText("Errore: Tutti i campi devono essere compilati.");
            successLabel.setText(""); // Pulisci eventuale messaggio di successo
            return;
        }

        /** Scrive la mail sul socket */ //MODIFICARE
        try  {
            Socket clientSocket = new Socket("localhost", Structures.PORT);
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());

            //Costruisco la richiesta
            Request<Mail> new_mail_to_send = new Request<>(Structures.SEND_MAIL,new_mail);
            output.writeObject(new_mail_to_send);

            // Se l'email è inviata con successo
            successLabel.setText("Email inviata con successo!");
            errorLabel.setText(""); // Pulisci eventuale messaggio di errore

        /** Pulisce i campi (opzionale) */
        toField.clear();
        receiversList.clear();
        subjectField.clear();
        bodyArea.clear();
    } catch (IOException e) {
        // Gestione dell'errore durante l'invio
        errorLabel.setText("Errore durante l'invio dell'email: " + e.getMessage());
        successLabel.setText(""); // Cancella eventuali successi
    }
}

    /** Funzione per tornare alla sezione Inbox */
    @FXML
    public void goBack() {
        InboxController inbox_controller = Structures.change_scene((Stage) successLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Inbox.fxml")));
        inbox_controller.access_inbox(backup);
    }
}
