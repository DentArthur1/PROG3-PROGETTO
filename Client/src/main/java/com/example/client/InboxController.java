package com.example.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.io.IOException;

public class InboxController {

    @FXML
    private Label connectionStatus;

    @FXML
    protected void handleLogout() {
        try {
            // Close the current window using connectionStatus label as a reference
            Stage currentStage = (Stage) connectionStatus.getScene().getWindow();
            currentStage.close();

            // Load Login.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Set up a new stage for Login.fxml and show it
            Stage loginStage = new Stage();
            loginStage.setScene(scene);
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCompose() {
        // Logic for composing an email
        try {
            // Load Send.fxml
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Send.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Set up a new stage for Send.fxml and show it
            Stage sendStage = new Stage();
            sendStage.setScene(scene);
            sendStage.setTitle("Scrivi");
            sendStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleDelete() {
        // Logic for deleting an email
    }

    @FXML
    protected void handleEmailClick() {
        // Logic for handling email click events
    }
}
