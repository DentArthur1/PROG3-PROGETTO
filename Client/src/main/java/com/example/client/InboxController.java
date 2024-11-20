package com.example.client;

import com.example.client.modules.Mail;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class InboxController {

    @FXML
    private Label connectionStatus;

    @FXML
    private VBox emailListContainer;

    private ObservableList<Mail> emailList;

    @FXML
    public void initialize() {
        // Inizializza la lista delle email
        emailList = FXCollections.observableArrayList();
        generateRandomEmails(50); // Genera 50 email casuali e aggiungi gli elementi alla lista visiva

        // Impostiamo gli elementi visivi per ogni email
        for (Mail email : emailList) {
            addEmailToView(email);
        }
    }

    private void generateRandomEmails(int numEmails) {
        // Genera email casuali
        String[] subjects = {"Meeting Invitation", "Newsletter", "Job Offer", "Project Update", "Reminder"};
        String[] recipients = {"john@example.com", "jane@company.com", "admin@web.com", "user@domain.com"};

        for (int i = 0; i < numEmails; i++) {
            String recipient = recipients[(int) (Math.random() * recipients.length)];
            String subject = subjects[(int) (Math.random() * subjects.length)];
            emailList.add(new Mail(recipient, subject));  // Aggiungi la nuova email
        }
    }

    private void addEmailToView(Mail email) {
        HBox emailBox = new HBox(10);

        CheckBox selectBox = new CheckBox();
        selectBox.selectedProperty().bindBidirectional(email.selectedProperty());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Spazio orizzontale tra le colonne

        Text recipientText = new Text(email.getRecipient());
        recipientText.setFont(Font.font(14)); // Imposta il font più grande

        Text subjectText = new Text(email.getSubject());
        subjectText.setFont(Font.font(14)); // Imposta il font più grande

        // Separa l'indirizzo email dall'oggetto
        gridPane.add(recipientText, 0, 0);
        gridPane.add(subjectText, 0, 1);

        emailBox.getChildren().addAll(selectBox, gridPane);
        emailListContainer.getChildren().add(emailBox);
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
        // Rimuove le email selezionate
        ObservableList<Mail> selectedEmails = FXCollections.observableArrayList();
        for (Mail email : emailList) {
            if (email.isSelected()) {
                selectedEmails.add(email);
            }
        }
        emailList.removeAll(selectedEmails);  // Rimuove le email selezionate

        // Aggiorna la vista della lista delle email
        emailListContainer.getChildren().clear();
        for (Mail email : emailList) {
            addEmailToView(email);
        }
    }
}
