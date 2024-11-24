package com.example.client.modules;

import com.example.client.HelloApplication;
import com.example.client.InboxController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import com.example.client.modules.SessionBackup;

public class Structures {
    //Classe di supporto per la definizione di costanti, import e funzioni

    public static final int SCENE_WIDTH = 500;
    public static final int SCENE_HEIGHT = 500;
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static <T> T change_scene(String new_scene_fxml, Stage current_stage,  Class<?> current_class){
        //Metodo utilizzato per cambiare scena e ritorna l'istanza del nuovo controller
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(current_class.getResource(new_scene_fxml));
            Scene scene = new Scene(fxmlLoader.load(), SCENE_WIDTH , SCENE_HEIGHT);
            current_stage.setScene(scene);
            current_stage.show();
            // Ottieni il controller della scena appena caricata
            T controller = fxmlLoader.getController();

            return controller;

        } catch (IOException e){
            System.out.println("Exeption caught while changing scene");
            return null;
        }
        //DOPO AVER CAMBIATO SCENA È NECESSARIO "PASSARE" AL NUOVO CONTROLLER IL BACKUP
    }

    public static void go_to_inbox(Stage current_stage,  Class<?> current_class, SessionBackup backup){
        //Funzione wrapper per semplificare il ritorno alla inbox
        InboxController inbox_controller = change_scene("Inbox.fxml", current_stage, current_class);
        inbox_controller.access_inbox(backup);
    }

    public static boolean isValidEmail(String email) {
        //Metodo per la verifica della sintassi delle mail
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static ObservableList<Mail> generateRandomEmails(int numEmails) {
        //Genero randomicamente le mail
        Random random = new Random();
        String[] subjects = {"Meeting Invitation", "Newsletter", "Job Offer", "Project Update", "Reminder"};
        String[] recipients = {"john@example.com", "jane@company.com", "admin@web.com", "user@domain.com"};
        LocalDate date_prova = generateRandomDate(LocalDate.of(2000, 1, 1), LocalDate.of(2023, 12, 31));
        ObservableList<Mail> mails = FXCollections.observableArrayList();

        for (int i = 0; i < numEmails; i++) {
            // Seleziona un mittente casuale
            String sender = recipients[random.nextInt(recipients.length)];
            String subject = subjects[random.nextInt(subjects.length)];
            // Genera un numero casuale di destinatari, minimo 1, massimo 3
            int numRecipients = random.nextInt(recipients.length);
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
            mails.add(new Mail(sender, subject, date_prova, new_recipients));
        }
        return mails;
    }

    private static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        // Converti le date in epoch days (giorni dall'inizio dell'epoca, 1970-01-01)
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = endDate.toEpochDay();
        // Genera un giorno randomico nell'intervallo
        long randomEpochDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        // Converte il giorno randomico in una LocalDate
        return LocalDate.ofEpochDay(randomEpochDay);
    }


}
