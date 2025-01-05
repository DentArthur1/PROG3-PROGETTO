package com.example.client;

import com.example.shared.Mail;
import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.stage.Stage;
import javafx.application.Platform;

import java.net.Socket;
import java.util.*;
import java.time.LocalDateTime;

import org.json.JSONArray;
import org.json.JSONObject;

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
    @FXML
    public TableColumn<Mail, String> previewColumn;
    public SessionBackup backup;
    private Timer pingTimer;
    private Timer emailUpdateTimer;
    @FXML
    public Label errorLabel;

    /**
     * @param email è l'email dell'utente per la quale si vuole visualizzare la casella di posta
     */

    @FXML
    public void set_user_email(String email) {
        userMail.setText("" + email);
    }

    /** inizializzazione controller */

    public void initialize() {
        /** Colonna con checkbox per selezionare le email */
        TableColumn<Mail, Boolean> selectColumn = new TableColumn<>("Seleziona");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(column -> createCheckboxCell());

        /** Aggiungi la colonna "Seleziona" come prima colonna */
        emailTable.getColumns().add(0, selectColumn);

        /** Configura le altre colonne */
        receiversColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        previewColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getContent()));
        /** Per prevenire l'aggiunta di nuove colonne inutili */
        emailTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        /** Inizializza il ping task */
        startPingTask();
        startEmailUpdateTask();
    }

    /** Metodo per creare una cella con checkbox per selezionare le email
     * @return della cella con checkbox
     */
    private TableCell<Mail, Boolean> createCheckboxCell() {
        return new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            {
                checkBox.setOnAction(event -> {
                    Mail mail = getTableRow().getItem();
                    if (mail != null) {
                        mail.setSelected(checkBox.isSelected());
                    }
                });
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                }
            }
        };
    }


    /**
     * Metodo per accedere alla casella di posta (inbox)
     * @param sessionBackup è il backup della sessione
     */

    public void access_inbox(SessionBackup sessionBackup) {
        /** Accedo a una sessione di Inbox esistente o ne creo una nuova */
        if (!sessionBackup.isSessionStarted()) {
            /** La sessione non è ancora iniziata (è stata creata la classe in Login) */
            backup = sessionBackup;
            emailList = get_new_emails();
            sessionBackup.startSession(emailList);
            set_user_email(backup.getUserEmailBackup());
        } else {
            /** Una sessione è già attiva, procedo a ripristinarla */
            restore_inbox(sessionBackup);
        }
        /** Rendo le email visibili a schermo */
        receiversColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        emailTable.setItems(emailList);
    }

    /** Avvia un task periodico per comunicare con il server*/
    private void startPingTask() {
        pingTimer = new Timer(true);
        pingTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateServerStatus();
            }
        }, 0, 500); // Ping every 0.5 seconds
    }

    /** Avvia un task periodico per aggiornare le email */
    private void startEmailUpdateTask() {
        emailUpdateTimer = new Timer(true); /** Timer come thread daemon */
        emailUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    ObservableList<Mail> updatedEmails = get_new_emails();
                    if (updatedEmails != null) {
                        for (Mail email : updatedEmails) {
                            if (!emailList.contains(email)) { /** Verifica se l'email è già presente */
                                emailList.add(email);        /** Aggiunge solo le nuove email */
                            }
                        }
                        backup.setEmailBackup(FXCollections.observableArrayList(emailList)); // Aggiorna il backup
                    }
                });
            }
        }, 1000, 1000); /** Aggiorna ogni secondo */
    }

    /**
     * Ferma l'aggiornamento delle mail quando cambio scena o faccio logout
     */
    private void stop_email_update(){
        // Fermiamo il timer di aggiornamento delle email
        if (emailUpdateTimer != null) {
            emailUpdateTimer.cancel();  // Interrompe il timer e i relativi thread
            emailUpdateTimer = null;    // Impostiamo il riferimento a null per evitare uso futuro
        }
    }

    /**
     * Ferma la funzione di ping del server
     */
    private void stop_ping_timer(){
        if (pingTimer != null) {
            pingTimer.cancel();
            pingTimer = null;
        }
    }

    private void updateServerStatus() {
        Platform.runLater(() -> {
            if (isServerActive()) {
                connectionStatus.setText("Attiva");
                connectionStatus.setStyle("-fx-text-fill: green;");
            } else {
                connectionStatus.setText("Non Attiva");
                connectionStatus.setStyle("-fx-text-fill: red;");
            }
        });
    }

    /**
     *  Funzione per verificare se il server è attivo
     *  @return true se il server è attivo, false altrimenti
     */
    private boolean isServerActive() {
        try {
            Socket clientSocket = new Socket("localhost", Structures.PORT);
            String ping_request = Structures.build_request(Structures.PING, "PING", backup.getUserEmailBackup());
            Structures.sendRequest(clientSocket, ping_request);
            JSONObject pong_response = Structures.wait_for_response(clientSocket);
            return pong_response.getInt(Structures.REQUEST_CODE_KEY) == Structures.PING;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Ottiene le nuove email dal server
     * @return la lista delle nuove email
     */

    private ObservableList<Mail> get_new_emails() {

        List<Integer> existingIds = new ArrayList<>();
        if (emailList != null){ //Dopo la prima run
            for (Mail mail : emailList) {
                existingIds.add(mail.getId());
            }
        }

        try {
            Socket clientSocket = new Socket("localhost", Structures.PORT);

            String mails_request = Structures.build_request(Structures.UPDATE_MAILS, existingIds.stream().mapToInt(Integer::intValue).toArray(), backup.getUserEmailBackup());
            Structures.sendRequest(clientSocket, mails_request);
            JSONObject mails_response = Structures.wait_for_response(clientSocket);

            return parse_mails(mails_response);
        } catch (Exception e) {
            System.out.println("Non è stato possibile recuperare le email. A causa di questo errore --->" + e.getMessage());
            return null;
        }

    }

    /**
     * Fa il parsing dell'array json delle mail ricevuto*/
    private ObservableList<Mail> parse_mails(JSONObject mails_response) {
        ObservableList<Mail> parsed_mails = FXCollections.observableArrayList();
        // Accesso al payload (array di messaggi)
        String payloadArray_string = mails_response.getString(Structures.REQUEST_PAYLOAD_KEY);
        JSONArray payload_array= new JSONArray(payloadArray_string);
        //Per ogni mail nell'array ne effettua il parsing e costruisce la classe mail
        for (int i = 0; i < payload_array.length(); i++) {
            JSONObject message = payload_array.getJSONObject(i);
            // Accesso ai campi del singolo messaggio
            String sender = message.getString(Structures.MAIL_SENDER_KEY);
            String title = message.getString(Structures.MAIL_TITLE_KEY);
            String content = message.getString(Structures.MAIL_CONTENT_KEY);
            String[] receivers = parse_receivers(message.getJSONArray(Structures.MAIL_RECEIVERS_KEY));
            LocalDateTime date = LocalDateTime.parse(message.getString(Structures.MAIL_DATE_KEY));
            int id = message.getInt(Structures.MAIL_ID_KEY);
            // Costruisco la mail e la aggiungo alla lista
            Mail new_parsed_mail = new Mail(sender, title, content, receivers, date, id);
            parsed_mails.add(new_parsed_mail);
        }
        return parsed_mails;
    }

    /**
     * Fa il parsing dei receivers ricevuti sotto forma di JSONArray*/
    private String[]  parse_receivers(JSONArray receivers){
        // Crea un array di stringhe con la dimensione dell'JSONArray
        String[] receiversArray = new String[receivers.length()];
        // Itera attraverso l'array JSON e copia ogni elemento nel nostro array di stringhe
        for (int i = 0; i < receivers.length(); i++) {
            receiversArray[i] = receivers.getString(i);
        }
        // Ritorna l'array di stringhe
        return receiversArray;
    }

    /** Viene gestito il click su una mail rendendo visibili i dettagli */

    @FXML
    protected void handleEmailClick() {
        stop_email_update();
        stop_ping_timer();
        /** Ottieni l'email selezionata dalla TableView */
        Mail selectedMail = emailTable.getSelectionModel().getSelectedItem();
        if (selectedMail != null) {
            /** Se ho selezionato una mail, entro nella sezione "Visione mail" */
            EmailController email_controller = Structures.change_scene((Stage) emailTable.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Email.fxml")));
            email_controller.set_email(selectedMail);
            email_controller.backup = backup; /** Passa il backup */
        }
    }

    /** Viene gestito il logout dell'utente */

    @FXML
    protected void handleLogout() {
        stop_email_update();
        stop_ping_timer();
        Structures.change_scene((Stage) emailTable.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Login.fxml")));
        try {
            Socket clientSocket = new Socket("localhost", Structures.PORT);
            String logout_request = Structures.build_request(Structures.LOGOUT,"", backup.getUserEmailBackup());
            Structures.sendRequest(clientSocket, logout_request);
        } catch (Exception e) {
            System.out.println("Failed requesting logout.");
        }
    }

    /** Accedo ad una schermata pe la composizione di una nuova mail */
    @FXML
    protected void handleCompose() {
        stop_email_update();
        stop_ping_timer();
        sendController send_controller = Structures.change_scene((Stage) emailTable.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Send.fxml")));
        send_controller.backup = backup;
    }

    /** Rimuove le email selezionate */
    @FXML
    protected void handleDelete() {
        // Ottengo le mail selezionate
        ArrayList<Mail> selectedEmails = new ArrayList<>();
        for (Mail email : emailList) {
            if (email.isSelected()) {
                selectedEmails.add(email);
            }
        }
        //ottengo l'array di id da eliminare
        ArrayList<Integer> mail_ids = get_mail_ids(selectedEmails);
        // Invio la richiesta di eliminazione al server
        try
          {
            Socket clientSocket = new Socket("localhost", Structures.PORT);
            String delete_request = Structures.build_request(Structures.DELETE, mail_ids, backup.getUserEmailBackup());
            Structures.sendRequest(clientSocket, delete_request);
            emailList.removeAll(selectedEmails);
        } catch (Exception e) {
            System.out.println("Failed deleting mails.");
        }
        // Aggiorna il backup per riflettere la nuova lista di email
        backup.setEmailBackup(FXCollections.observableArrayList(emailList));
        // Aggiorna la vista della lista delle email
        emailTable.refresh(); // Aggiorna la vista della TableView
    }

    /**
     * Ottengo un array di id delle mail selezionate
     * @param mails lista delle mail selezionate
     * @return l'array di id delle mail selezionate
     */
    private ArrayList<Integer> get_mail_ids(ArrayList<Mail> mails) {
        ArrayList<Integer> mail_ids = new ArrayList<>();
        for (Mail mail : mails) {
            mail_ids.add(mail.getId());
        }
        return mail_ids;
    }

    /**
     * Ripristina la inbox da un backup
     * @param sessionBackup è il backup della sessione
     */

    private void restore_inbox(SessionBackup sessionBackup) {
        /** Ripristino la visione delle mail secondo le specifiche del backup */
        backup = sessionBackup;
        this.emailList = backup.getEmailBackup();
        set_user_email(backup.getUserEmailBackup());
    }

    public void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    public void hideError() {
        errorLabel.setVisible(false);
    }


}