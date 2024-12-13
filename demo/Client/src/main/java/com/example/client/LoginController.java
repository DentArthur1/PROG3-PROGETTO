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

    public void handleLogin() {
        /** Funzione che giudica l'input dell'utente e agisce di conseguenza */
        user_mail = emailField.getText();

        if (!Structures.isValidEmail(user_mail)) {
            errorLabel.setText("Invalid email format. Please try again.");
        } else {
            try (Socket socket = new Socket("localhost", Structures.PORT);
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

                Request<String> request = new Request<>(Structures.LOGIN_CHECK, user_mail ,user_mail);
                output.writeObject(request);
                output.flush();

                Request<?> response = (Request<?>) input.readObject();
                if (response.getRequestCode() == Structures.LOGIN_OK) {
                    InboxController inbox_controller = Structures.change_scene((Stage) errorLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Inbox.fxml")));
                    SessionBackup backup = new SessionBackup(user_mail);
                    inbox_controller.access_inbox(backup);
                } else {
                    errorLabel.setText("Login failed. User not found.");
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                errorLabel.setText("Error connecting to server.");
            }
        }
    }
}