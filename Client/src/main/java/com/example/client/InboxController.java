package com.example.client;


import com.example.client.modules.Mail;
import com.example.client.modules.SessionBackup;
import com.example.client.modules.Structures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import java.util.*;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

import java.io.IOException;

public class InboxController {

    @FXML
    private Label connectionStatus;
    @FXML
    private VBox emailListContainer;

    private ObservableList<Mail> emailList;
    @FXML
    private Label userMail;

    @FXML
    private TableView<Mail> emailTable;

    @FXML
    private TableColumn<Mail, String> recipientColumn;

    @FXML
    private TableColumn<Mail, String> subjectColumn;

    public SessionBackup backup;

    @FXML
    public void set_user_email(String email) {
        userMail.setText("Benvenuto," + email);
    }

    public void initialize() {
        // Aggiungi una colonna per la checkbox
        TableColumn<Mail, Boolean> selectColumn = new TableColumn<>("Seleziona");

        // Collega la proprietà "selected" della Mail alla checkbox
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());

        // Usa una TableCell personalizzata con una CheckBox
        selectColumn.setCellFactory(column -> new TableCell<Mail, Boolean>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(checkBox);
                    checkBox.setSelected(item != null && item);
                    checkBox.setOnAction(event -> {
                        // Modifica lo stato di selezione dell'email
                        Mail mail = getTableRow().getItem();
                        if (mail != null) {
                            mail.setSelected(checkBox.isSelected());
                        }
                    });
                }
            }
        });

        // Aggiungi la colonna "Seleziona" alla TableView
        emailTable.getColumns().add(0, selectColumn);  // Aggiungi la colonna "Seleziona" come prima colonna

        // Imposta le colonne esistenti
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
    }
    public void access_session(SessionBackup sessionBackup) {
        if (sessionBackup == null) {
            System.err.println("Errore: Il backup passato è null.");
            return; // Evita di procedere
        }

        if (!sessionBackup.isSessionStarted()) {
            backup = sessionBackup;
            emailList = FXCollections.observableArrayList();
            generateRandomEmails(50);
            sessionBackup.setEmailBackup(emailList);
            sessionBackup.sessionStarted = true;
            set_user_email(backup.getUserEmailBackup());

        } else {
            restore_session(sessionBackup);
        }

        // Imposta le email nella TableView
        recipientColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSubject()));
        emailTable.setItems(emailList);
    }

    private void generateRandomEmails(int numEmails) {
        Random random = new Random();
        String[] subjects = {"Meeting Invitation", "Newsletter", "Job Offer", "Project Update", "Reminder"};
        String[] recipients = {"john@example.com", "jane@company.com", "admin@web.com", "user@domain.com"};
        LocalDate date_prova = generateRandomDate(LocalDate.of(2000, 1, 1), LocalDate.of(2023, 12, 31));

        for (int i = 0; i < numEmails; i++) {
            // Seleziona un mittente casuale
            String sender = recipients[random.nextInt(recipients.length)];
            String subject = subjects[random.nextInt(subjects.length)];
            // Genera un numero casuale di destinatari, minimo 1, massimo 3
            int numRecipients = random.nextInt(recipients.length); // Random tra 1 e 3
            // Creare una lista dalla lista dei destinatari
            ArrayList<String> list = new ArrayList<>(Arrays.asList(recipients));
            // Rimuovere il mittente dalla lista
            list.remove(sender);
            // Selezionare un numero casuale di destinatari, tra 1 e 3, senza includere il mittente
            ArrayList<String> selectedRecipients = new ArrayList<>();
            for (int j = 0; j < numRecipients && !list.isEmpty(); j++) {
                // Selezionare un destinatario casuale dalla lista aggiornata
                String recipient = list.remove(random.nextInt(list.size()));
                selectedRecipients.add(recipient);
            }
            // Convertire la lista dei destinatari selezionati in un array
            String[] new_recipients = selectedRecipients.toArray(new String[0]);
            // Creare una nuova email e aggiungerla alla lista
            emailList.add(new Mail(sender, subject, date_prova, new_recipients));
        }
    }

    private LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        // Converti le date in epoch days (giorni dall'inizio dell'epoca, 1970-01-01)
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        // Genera un giorno randomico nell'intervallo
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        // Converte il giorno randomico in una LocalDate
        return LocalDate.ofEpochDay(randomEpochDay);
    }

    private void addEmailToView(Mail email) {
        HBox emailBox = new HBox(10);

        CheckBox selectBox = new CheckBox();
        selectBox.selectedProperty().bindBidirectional(email.selectedProperty());

        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Spazio orizzontale tra le colonne

        Text recipientText = new Text(email.getSender());
        recipientText.setFont(Font.font(14)); // Imposta il font più grande

        Text subjectText = new Text(email.getSubject());
        subjectText.setFont(Font.font(14)); // Imposta il font più grande

        // Separa l'indirizzo email dall'oggetto
        gridPane.add(recipientText, 0, 0);
        gridPane.add(subjectText, 0, 1);

        //emailBox.getChildren().addAll(selectBox, gridPane);
        //emailListContainer.getChildren().add(emailBox);
    }

    @FXML
    protected void handleEmailClick() {
        // Ottieni l'email selezionata dalla TableView
        Mail selectedMail = emailTable.getSelectionModel().getSelectedItem();

        if (selectedMail != null) {
            EmailController email_controller = Structures.change_scene("Email.fxml", (Stage) emailTable.getScene().getWindow(), getClass());
            email_controller.set_email(selectedMail);
            email_controller.backup = backup; // Passa il backup
        } else {
            System.err.println("Errore: Nessuna email selezionata.");
        }
    }

    @FXML
    protected void handleLogout() {
        Structures.change_scene("Login.fxml",(Stage) emailTable.getScene().getWindow(), getClass());
    }

    @FXML
    protected void handleCompose() {
        sendController send_controller = Structures.change_scene("Send.fxml", (Stage) emailTable.getScene().getWindow(), getClass());
        send_controller.backup = backup;
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
        //emailListContainer.getChildren().clear();
        for (Mail email : emailList) {
            addEmailToView(email);
        }

    }

    private void restore_session(SessionBackup sessionBackup) {
        backup = sessionBackup;
        this.emailList = backup.getEmailBackup();
        set_user_email(backup.getUserEmailBackup());
    }
}
