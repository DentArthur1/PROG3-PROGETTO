package com.example.client;

import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class sendController {
    /** Classe controllore per gestire l'operazione di invio messaggi
     * Questa classe consente all'utente di comporre, inviare email e gestire i destinatari.
     */

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

    /**
     * Imposta l'oggetto dell'email per il metodo di risposta
     * @param subject L'oggetto dell'email
     */
    public void set_subject(String subject) {
        subjectField.setText("RE:" + subject);
    }

    /**
     * Imposta il contenuto dell'email per il metodo di risposta
     * @param content Il contenuto dell'email
     */
    public void set_content(String content) {
        bodyArea.setText("RE:" + content);
    }

    /**
     * Imposta i destinatari dell'email
     * @param receivers è un array di stringhe contenente gli indirizzi email dei destinatari
     */
    public void set_receivers(String[] receivers) {
        for (int i = 0; i < receivers.length; i++) {
            toField.appendText(receivers[i]);
            // Aggiungi la virgola solo se non è l'ultimo elemento
            if (i < receivers.length - 1) {
                toField.appendText(",");
            }
        }
    }

    /**
     * Aggiunge i destinatari alla lista dei destinatari.
     * Verifica la validità degli indirizzi email prima di aggiungerli.
     */
    @FXML
    protected void addReceiver() {
        String[] destinatari = toField.getText().trim().split(",");

        for (String destinatario : destinatari) {
            /** Verifica se il campo del destinatario non è vuoto e se l'indirizzo è valido */
            if (!destinatario.isEmpty() && Structures.isValidEmail(destinatario)) {
                try (Socket socket = new Socket("localhost", Structures.PORT)){

                    // Crea una richiesta per verificare l'esistenza del destinatario
                    String dest_check_request = Structures.build_request(Structures.DEST_CHECK, destinatario, backup.getUserEmailBackup());
                    // Invia la richiesta
                    Structures.sendRequest(socket, dest_check_request);
                    // Ricevi la risposta dal server
                    JSONObject dest_check_response = Structures.wait_for_response(socket);
                    if (dest_check_response.getInt(Structures.REQUEST_CODE_KEY) == Structures.DEST_OK) {
                        // Aggiungi il destinatario alla lista visibile
                        receiversList.appendText(destinatario + "\n");
                        // Cancella il campo per il prossimo inserimento
                        toField.clear();
                    } else if (dest_check_response.getInt(Structures.REQUEST_CODE_KEY) == Structures.DEST_ERROR) {
                        errorLabel.setText("Errore: Indirizzo email: " + destinatario + " non trovato.");
                        successLabel.setText(""); // Cancella eventuali successi precedenti
                    } else {
                        errorLabel.setText("Errore sconosciuto.");
                        successLabel.setText(""); // Cancella eventuali successi precedenti
                        break;
                    }
                } catch (IOException e) {
                    errorLabel.setText("Errore di connessione al server.");
                    successLabel.setText("");
                    break;
                }
            } else {
                errorLabel.setText("Errore: Indirizzo email non valido.");
                successLabel.setText(""); // Cancella eventuali successi precedenti
                break;
            }
        }
    }

    /**
     * Invio dell'email
     *
     * Verifica che tutti i campi siano compilati correttamente e invia l'email
     * al server tramite una connessione socket
     */

    @FXML
    protected void sendEmail() {
        String oggetto = subjectField.getText().trim();
        // Elimino eventuali caratteri che romperebbero la formattazione del file
        String corpo = bodyArea.getText().trim().replace("\n", "").replace("\r", "");

        String[] destinatari = receiversList.getText().trim().split("\n"); // Legge tutti i destinatari
        System.out.println(Arrays.toString(destinatari));
        LocalDateTime date = LocalDateTime.now();

        /** Controllo della correttezza degli input */
        if (destinatari.length == 0 || oggetto.isEmpty() || corpo.isEmpty()) {
            errorLabel.setText("Errore: Tutti i campi devono essere compilati.");
            successLabel.setText(""); // Pulisci eventuale messaggio di successo
            return;
        }

        /** Scrive la mail sul socket */
        try (Socket clientSocket = new Socket("localhost", Structures.PORT)) {

            // Costruisco la richiesta
            String new_mail_to_send = Structures.build_request(Structures.SEND_MAIL,
                    Structures.build_mail(backup.getUserEmailBackup(),
                            oggetto,
                            corpo,
                            destinatari,
                            date,
                            Structures.generateUniqueInteger(date, backup.getUserEmailBackup()
                            )), backup.getUserEmailBackup());

            //Invio la richiesta
            if(Structures.sendRequest(clientSocket,new_mail_to_send)){
                // Se l'email è inviata con successo
                successLabel.setText("Email inviata con successo!");
                errorLabel.setText(""); // Pulisci eventuale messaggio di errore

                // Pulisce i campi
                toField.clear();
                receiversList.clear();
                subjectField.clear();
                bodyArea.clear();
            } else {
                errorLabel.setText("Errore durante l'invio dell'email (sendRequest ha ritornato falso)");
            }

        } catch (IOException e) {
            // Gestione dell'errore durante l'invio
            errorLabel.setText("Errore durante l'apertura del socket o la costruzione della richiesta per l'invio di una mail" + e.getMessage());
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