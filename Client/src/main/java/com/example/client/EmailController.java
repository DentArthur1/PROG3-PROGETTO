package com.example.client;

import com.example.client.modules.Mail;
import com.example.client.modules.SessionBackup;
import com.example.client.modules.Structures;
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
        InboxController inbox_controller = Structures.change_scene("Inbox.fxml", (Stage) subjectLabel.getScene().getWindow(), getClass());
        inbox_controller.access_session(backup);
    }


    public void handleReply(ActionEvent actionEvent) {
        sendController send_controller = Structures.change_scene("Send.fxml", (Stage) subjectLabel.getScene().getWindow(), getClass());
        send_controller.backup = backup;
    }

    public void handleReplyAll(ActionEvent actionEvent) {
    }
}