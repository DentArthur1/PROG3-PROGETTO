package com.example.client;

import com.example.client.modules.Mail;
import com.example.client.modules.SessionBackup;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;


public class EmailController {

    public Mail selected_email;

    @FXML
    public Label senderLabel;

    @FXML
    public Label receiverLabel;

    @FXML
    public Label subjectLabel;

    @FXML
    public Label dateLabel;

    public SessionBackup backup;


    public void set_email(Mail example){
        this.selected_email = example;
        fill_data(example);
    }

    public void fill_receivers(Mail example){
        String[] receivs = example.getReceivers();
        String all_Receivs = String.join(", ", receivs);
        receiverLabel.setText(all_Receivs);
    }

    public void fill_data(Mail example){
        dateLabel.setText(example.getDate().toString());
        subjectLabel.setText(example.getSubject());
        senderLabel.setText(example.getSender());
        fill_receivers(example);
    }

    public void handleBackToInbox(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Inbox.fxml"));
            Scene inboxScene = new Scene(fxmlLoader.load());

            // Ottieni il controller associato alla nuova scena
            InboxController controller = fxmlLoader.getController();

            if (backup == null) {
                System.err.println("Errore: Il backup è null in EmailController.");
            } else {
                controller.access_session(backup); // Ripristina il backup
            }

            // Cambia scena
            Stage currentStage = (Stage) subjectLabel.getScene().getWindow();
            currentStage.setScene(inboxScene);
            currentStage.setTitle("Inbox");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void handleReply(ActionEvent actionEvent) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Send.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            Stage currentStage = (Stage) subjectLabel.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("Send");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleReplyAll(ActionEvent actionEvent) {
    }
}