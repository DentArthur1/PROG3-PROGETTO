package com.example.client;

import com.example.client.modules.Mail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;

public class InboxController {

    @FXML
    private Label connectionStatus;

    @FXML
    private TableView<Mail> emailTable;

    @FXML
    private TableColumn<Mail, String> recipientColumn;

    @FXML
    private TableColumn<Mail, String> subjectColumn;

    private ObservableList<Mail> emailList;

    @FXML
    public void initialize() {
        // Initialize the TableView with random data
        emailList = FXCollections.observableArrayList();
        generateRandomEmails(50); // Generate 50 random emails

        // Bind the columns to the properties of the Mail object
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRecipient()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));

        // Set the items of the TableView to the ObservableList
        emailTable.setItems(emailList);

        // Impostare la larghezza delle colonne per l'espansione automatica
        subjectColumn.prefWidthProperty().bind(emailTable.widthProperty().multiply(0.3)); // 30% della larghezza della TableView
        recipientColumn.prefWidthProperty().bind(emailTable.widthProperty().multiply(0.3)); // 30% per recipient

        // Impostare la politica di ridimensionamento per adattarsi al contenuto
        emailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void generateRandomEmails(int numEmails) {
        Random random = new Random();
        String[] subjects = {"Meeting Invitation", "Newsletter", "Job Offer", "Project Update", "Reminder"};
        String[] recipients = {"john@example.com", "jane@company.com", "admin@web.com", "user@domain.com"};

        for (int i = 0; i < numEmails; i++) {
            String recipient = recipients[random.nextInt(recipients.length)];
            String subject = subjects[random.nextInt(subjects.length)];
            emailList.add(new Mail(recipient, subject));
        }
    }

    @FXML
    protected void handleLogout() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Scene loginScene = new Scene(fxmlLoader.load());

            Stage currentStage = (Stage) connectionStatus.getScene().getWindow();
            currentStage.setScene(loginScene);
            currentStage.setTitle("Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void handleCompose() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Send.fxml"));
            Scene sendScene = new Scene(fxmlLoader.load());

            Stage currentStage = (Stage) connectionStatus.getScene().getWindow();
            currentStage.setScene(sendScene);
            currentStage.setTitle("Scrivi");
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
