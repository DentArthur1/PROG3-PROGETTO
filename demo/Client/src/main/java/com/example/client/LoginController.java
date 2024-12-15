package com.example.client;

import com.example.shared.Request;
import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class LoginController {
    /** Classe controllore per gestire il processo di login, campo di testo dell'email,
     * etichetta per la visualizzazione di errori di login ed email dell'utente  */
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;

    public String user_mail;

    /** Metodo che gestisce l'input dell'utente e agisce di conseguenza
     * Questo metodo viene chiamato quando l'utente preme il pulsante di login.
     * Verifica l'input dell'utente e tenta di autenticare l'utente attraverso
     * una connessione al server.
     */
    public void handleLogin() {
        user_mail = emailField.getText().trim();

        // Controlla se l'email inserita è in un formato valido.
        if (!Structures.isValidEmail(user_mail)) {
            errorLabel.setText("Invalid email format. Please try again.");
        } else {
            // Tenta di connettersi al server per verificare l'email.
            try (Socket socket = new Socket("localhost", Structures.PORT);
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

                // Crea una richiesta di verifica del login con l'email dell'utente.
                Request<String> request = new Request<>(Structures.LOGIN_CHECK, user_mail, user_mail);
                output.writeObject(request);
                output.flush();

                // Riceve la risposta dal server
                Request<?> response = (Request<?>) input.readObject();
                // Controlla il codice di risposta ricevuto dal server.
                if (response.getRequestCode() == Structures.LOGIN_OK) {
                    // Se l'utente è stato trovato, cambia la scena alla casella di posta.
                    InboxController inbox_controller = Structures.change_scene((Stage) errorLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Inbox.fxml")));
                    SessionBackup backup = new SessionBackup(user_mail);
                    inbox_controller.access_inbox(backup);
                } else {
                    // Se l'utente non è stato trovato, visualizza un messaggio di errore
                    errorLabel.setText("Login failed. User not found.");
                }
            } catch (IOException | ClassNotFoundException e) {
                // gestisce gli errori di connessione al server
                e.printStackTrace();
                errorLabel.setText("Error connecting to server.");
            }
        }
    }
}