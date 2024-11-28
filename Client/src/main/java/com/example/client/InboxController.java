package com.example.client;


import com.example.client.modules.Mail;
import com.example.client.modules.SessionBackup;
import com.example.client.modules.Structures;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.*;
import javafx.beans.property.SimpleStringProperty;


public class InboxController {
    //Classe controllore per la gestione dell'Inbox
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

    @FXML
    public void set_user_email(String email) {
        userMail.setText("Benvenuto," + email);
    }

    public void initialize() {
        // Colonna con checkbox per selezionare le email
        TableColumn<Mail, Boolean> selectColumn = new TableColumn<>("Seleziona");
        selectColumn.setCellValueFactory(cellData -> cellData.getValue().selectedProperty());
        selectColumn.setCellFactory(column -> createCheckboxCell());

        // Aggiungi la colonna "Seleziona" come prima colonna
        emailTable.getColumns().add(0, selectColumn);

        // Configura le altre colonne
        receiversColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
    }

    // Metodo per creare una cella con checkbox
    private TableCell<Mail, Boolean> createCheckboxCell() {
        return new TableCell<>() {
            private final CheckBox checkBox = new CheckBox();

            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null); // Nessuna checkbox per celle vuote
                } else {
                    setGraphic(checkBox); // Mostra la checkbox
                    checkBox.setSelected(item != null && item); // Sincronizza lo stato
                    checkBox.setOnAction(event -> {
                        Mail mail = getTableRow().getItem(); // Ottieni l'email associata alla riga
                        if (mail != null) {
                            mail.setSelected(checkBox.isSelected()); // Aggiorna la proprietà "selected"
                        }
                    });
                }
            }
        };
    }

    public void access_inbox(SessionBackup sessionBackup) {
        //Accedo a una sessione di Inbox esistente o ne creo una nuova
        if (!sessionBackup.isSessionStarted()) {
            //La sessione non è ancora iniziata(è stata creata la classe in Login)
            backup = sessionBackup;
            emailList = Structures.generateRandomEmails(50);
            sessionBackup.startSession(emailList);
            set_user_email(backup.getUserEmailBackup());
        } else {
            //Una sessione è già attiva, procedo a ripristinarla
            restore_inbox(sessionBackup);
        }
        //Rendo le email visibili a schermo
        receiversColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSender()));
        subjectColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        emailTable.setItems(emailList);
    }

    private void addEmailToView(Mail email) {
        //Rende visibile la mail i-esima
        HBox emailBox = new HBox(10);
        //Proprietà della checkbox
        CheckBox selectBox = new CheckBox();
        selectBox.selectedProperty().bindBidirectional(email.selectedProperty());
        //Proprietà dei campi dati delle mail
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10); // Spazio orizzontale tra le colonne

        Text recipientText = new Text(email.getSender());
        recipientText.setFont(Font.font(14)); // Imposta il font più grande

        Text subjectText = new Text(email.getTitle());
        subjectText.setFont(Font.font(14)); // Imposta il font più grande
        // Separa l'indirizzo email dall'oggetto
        gridPane.add(recipientText, 0, 0);
        gridPane.add(subjectText, 0, 1);

    }

    @FXML
    protected void handleEmailClick() {
        //Ottieni l'email selezionata dalla TableView
        Mail selectedMail = emailTable.getSelectionModel().getSelectedItem();

        if (selectedMail != null) {
            //Se ho selezionato una mail, entro nella sezione "Visione mail"
            EmailController email_controller = Structures.change_scene("Email.fxml", (Stage) emailTable.getScene().getWindow(), getClass());
            email_controller.set_email(selectedMail);
            email_controller.backup = backup; // Passa il backup
        }
    }

    @FXML
    protected void handleLogout() {
        //Torno alla schermata di login
        Structures.change_scene("Login.fxml",(Stage) emailTable.getScene().getWindow(), getClass());
    }

    @FXML
    protected void handleCompose() {
        //Accedo alla sezione "Composizione mail"
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
        emailList.removeAll(selectedEmails);

        // Aggiorna la vista della lista delle email
        for (Mail email : emailList) {
            addEmailToView(email);
        }

    }

    private void restore_inbox(SessionBackup sessionBackup) {
        //Ripristino la visione delle mail secondo le specifiche del backup
        backup = sessionBackup;
        this.emailList = backup.getEmailBackup();
        set_user_email(backup.getUserEmailBackup());
    }
}
