package com.example.client;

import com.example.shared.Request;
import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private Label errorLabel;

    public String user_mail;

    public void handleLogin() {
        user_mail = emailField.getText().trim();

        if (!Structures.isValidEmail(user_mail)) {
            errorLabel.setText("Invalid email format. Please try again.");
        } else {
            try (Socket socket = new Socket("localhost", Structures.PORT);
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                 BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

                Request<String> request = new Request<>(Structures.LOGIN_CHECK, user_mail, user_mail);
                writer.write(request.toJson());
                writer.newLine();
                writer.flush();

                String responseJson = reader.readLine();
                if (responseJson != null && !responseJson.isEmpty()) {
                    Request<?> response = Request.fromJson(responseJson);

                    if (response.getRequestCode() == Structures.LOGIN_OK) {
                        InboxController inbox_controller = Structures.change_scene((Stage) errorLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Inbox.fxml")));
                        SessionBackup backup = new SessionBackup(user_mail);
                        inbox_controller.access_inbox(backup);
                    } else {
                        errorLabel.setText("Login failed. User not found.");
                    }
                } else {
                    errorLabel.setText("Error: Received empty response from server.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                errorLabel.setText("Error connecting to server.");
            }
        }
    }
}