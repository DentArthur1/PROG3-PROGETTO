package com.example.client;

import com.example.shared.Mail;
import com.example.shared.SessionBackup;
import com.example.shared.Structures;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Arrays;


public class EmailController {
    /** Classe controllore email per gestire l'operazione di visualizzazione mail */

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

    @FXML
    public Label bodyLabel;


    public void set_email(Mail example){
        /** Funzione usata per selezionare l'email da visualizzare */
        this.selected_email = example;
        fill_data(example);
    }

    public void fill_receivers(Mail example){
        /** Ottiene i destinatari dai dati della classe mail e li mostra a schermo */
        String[] receivs = example.getReceivers();
        String all_Receivs = String.join(",", receivs);
        receiverLabel.setText(all_Receivs);
    }

    public void fill_data(Mail example){
        /** Riempe le caselle con i dati della mail */
        dateLabel.setText(example.getDate().toString());
        subjectLabel.setText(example.getTitle());
        senderLabel.setText(example.getSender());
        bodyLabel.setText(example.getContent());
        fill_receivers(example);
    }

    public void handleBackToInbox(ActionEvent actionEvent) {
        InboxController inbox_controller = Structures.change_scene((Stage) subjectLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Inbox.fxml")));
        inbox_controller.access_inbox(backup);
    }


    public void handleReplyAll(ActionEvent actionEvent) {
        /**
         * BUG: COPIA ERRATA DELLE MAIL CON PIÙ DESTINATARI SUL FILE*/
        /** Accede alla sezione Send */
        sendController send_controller = Structures.change_scene((Stage) subjectLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Send.fxml")));
        send_controller.backup = backup;
        /**
         * Setto i valori della mail alla quale intendo rispondere*/
        send_controller.set_content(bodyLabel.getText());
        send_controller.set_subject(subjectLabel.getText());
        ArrayList<String> receivers = new ArrayList<>(Arrays.asList(selected_email.getReceivers()));
        /**
         * Aggiunge tutti i destinatari, rimuove la propria mail, aggiunge il mittente alla lista dei nuovi destinatari*/
        receivers.remove(backup.getUserEmailBackup());
        receivers.add(senderLabel.getText());
        send_controller.set_receivers(receivers.toArray(new String[0]));
    }

    public void handleReply(ActionEvent actionEvent) {
        /** Accede alla sezione Send */
        sendController send_controller = Structures.change_scene((Stage) subjectLabel.getScene().getWindow(), new FXMLLoader(EmailController.class.getResource("Send.fxml")));
        send_controller.backup = backup;
        /**
         * Setto i valori della mail alla quale intendo rispondere*/
        send_controller.set_content(bodyLabel.getText());
        send_controller.set_subject(subjectLabel.getText());
        String[] receiver = {selected_email.getSender()};
        send_controller.set_receivers(receiver);
    }
}