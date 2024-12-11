package com.example.client;

import com.example.shared.Mail;
import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import com.example.shared.Request;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.time.LocalDateTime;

public class InboxController {
    /**
     * Classe controllore per la gestione dell'Inbox
     */
    @FXML
    private Label connectionStatus;
    private ObservableList<Mail> emailList;
    @FXML
    private Label userMail;
    @FXML
    private TableView<Mail> emailTable;
    @FXML
    private TableColumn<Mail, String> receiversColumn;
    @FXML
    private TableColumn<Mail, String> subjectColumn;
    public SessionBackup backup;
    private Timer pingTimer;
    private Timer emailUpdateTimer;

    @FXML
    public void set_user_email(String email) {
        userMail.setText("Benvenuto," + email);
    }

    public void initialize() {
        /** Colonna con checkbox per selezionare le email */
        TableColumn<Mail, Boolean> selectColumn = new TableColumn<>("Seleziona");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(column -> createCheckboxCell());

        /** Aggiungi la colonna "Seleziona" come prima colonna */
        emailTable.getColumns().add(0, selectColumn);

        // Configura le altre colonne
        receiversColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));

        // Inizializza il ping task
        startPingTask();
        startEmailUpdateTask();
    }

    /**
     * Metodo per creare una cella con checkbox
     */
    private TableCell<Mail, Boolean> createCheckboxCell() {
        return new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); /** Nessuna checkbox per celle vuote */
                } else {
                    setGraphic(checkBox); /** Mostra la checkbox */
                    checkBox.setSelected(item != null && item); /** Sincronizza lo stato */
                    checkBox.setOnAction(event -> {
                        Mail mail = getTableRow().getItem(); /** Ottieni l'email associata alla riga */
                        if (mail != null) {
                            mail.setSelected(checkBox.isSelected()); /** Aggiorna la proprietà "selected" */
                        }
                    });
                }
            }
        };
    }

    public void access_inbox(SessionBackup sessionBackup) {
        /** Accedo a una sessione di Inbox esistente o ne creo una nuova */
        if (!sessionBackup.isSessionStarted()) {
            //La sessione non è ancora iniziata(è stata creata la classe in Login)
            backup = sessionBackup;
            emailList = get_new_emails();
            sessionBackup.startSession(emailList);
            set_user_email(backup.getUserEmailBackup());
        } else {
            /** Una sessione è già attiva, procedo a ripristinarla */
            restore_inbox(sessionBackup);
        }
        //Rendo le email visibili a schermo
        receiversColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        emailTable.setItems(emailList);
    }

    private void startPingTask() {
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateServerStatus();
            }
        }, 0, 5000); // Ping every 5 seconds
    }
    private void startEmailUpdateTask() {
        /** Avvia un task periodico per aggiornare le email */
        emailUpdateTimer = new Timer(true); // Timer come thread daemon
        emailUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ObservableList<Mail> updatedEmails = get_new_emails();
                    if (updatedEmails != null) {
                        for (Mail email : updatedEmails) {
                            if (!emailList.contains(email)) { // Verifica se l'email è già presente
                                emailList.add(email);        // Aggiunge solo le nuove email
                            }
                        }
                        backup.setEmailBackup(FXCollections.observableArrayList(emailList)); // Aggiorna il backup
                    }
                });
            }
        }, 10000, 10000); // Aggiorna ogni 10 secondi
    }

    private void updateServerStatus() {
        Platform.runLater(() -> {
            if (isServerActive()) {
                connectionStatus.setText("Connessione: Attiva");
                connectionStatus.setStyle("-fx-text-fill: green;");
            } else {
                connectionStatus.setText("Connessione: Non Attiva");
                connectionStatus.setStyle("-fx-text-fill: red;");
            }
        });
    }

    private boolean isServerActive() {
        try {
            Socket clientSocket = new Socket("localhost", Structures.PORT);
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            Request<String> ping_request = new Request<>(Structures.PING, "ping");
            output.writeObject(ping_request);
            Request<?> pong_request = (Request<?>) input.readObject();

            return pong_request.getRequestCode() == Structures.PING;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private ObservableList<Mail> get_new_emails() {
        /** Funzione per ottenere le nuove email dal server da mostrare a schermo */
        ObservableList<Mail> parsed_mails = FXCollections.observableArrayList();

        try {
            Socket clientSocket = new Socket("localhost", Structures.PORT);
            ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());

            Request<String> mail_update_request = new Request<>(Structures.UPDATE_MAILS, backup.getUserEmailBackup());
            output.writeObject(mail_update_request);

            Request<?> new_mails = (Request<?>) input.readObject();


            if (new_mails.getRequestCode() == Structures.UPDATE_MAILS) {
                ArrayList<Mail> mails = (ArrayList<Mail>) new_mails.getPayload();
                //---
                for (Mail mail : mails) { //RECUPERO IL CAMPO PERSO DURANTE LA SERIALIZZAZIONE
                    mail.recover_from_serialization();
                }
                //---
                parsed_mails.addAll(mails);
            }
        } catch (Exception e) {
            System.out.println("Non è stato possibile recuperare le email. Riprova più tardi.");
        }
        return parsed_mails;
    }

    @FXML
    protected void handleEmailClick() {
        /** Ottieni l'email selezionata dalla TableView */
        Mail selectedMail = emailTable.getSelectionModel().getSelectedItem();

        if (selectedMail != null) {
            /** Se ho selezionato una mail, entro nella sezione "Visione mail" */
            EmailController email_controller = Structures.change_scene((Stage) emailTable.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Email.fxml")));
            email_controller.set_email(selectedMail);
            email_controller.backup = backup; /** Passa il backup */
        }
    }

    @FXML
    protected void handleLogout() {
        /** Torno alla schermata di login */
        Structures.change_scene((Stage) emailTable.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Login.fxml")));
    }

    @FXML
    protected void handleCompose() {
        /** Accedo alla sezione "Composizione mail" */
        sendController send_controller = Structures.change_scene((Stage) emailTable.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Send.fxml")));
        send_controller.backup = backup;
    }

    @FXML
    protected void handleDelete() {
        /** Rimuove le email selezionate */
        ObservableList<Mail> selectedEmails = FXCollections.observableArrayList();
        for (Mail email : emailList) {
            if (email.isSelected()) {
                selectedEmails.add(email);
            }
        }
        emailList.removeAll(selectedEmails);

        /** Aggiorna il backup per riflettere la nuova lista di email */
        backup.setEmailBackup(FXCollections.observableArrayList(emailList));

        /** Aggiorna la vista della lista delle email */
        emailTable.refresh(); // Aggiorna la vista della TableView
    }

    private void restore_inbox(SessionBackup sessionBackup) {
        /** Ripristino la visione delle mail secondo le specifiche del backup */
        backup = sessionBackup;
        this.emailList = backup.getEmailBackup();
        set_user_email(backup.getUserEmailBackup());
    }
}