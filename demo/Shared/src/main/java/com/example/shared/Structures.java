package com.example.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

public class Structures {
    /**
     * Classe di supporto per la definizione di costanti, import e funzioni
     * @param EMAIL_REGEX: espressione regolare per la validazione delle email, prende solo domini con almeno 2 caratteri e con un solo punto
     * @param PORT: porta del server
     */
    /** Dimensioni della scena */
    public static final int SCENE_WIDTH = 650; /** Larghezza della scena*/
    public static final int SCENE_HEIGHT = 500; /** Altezza della scena*/
    /** Espressione regolare per la validazione delle email */
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final int PORT = 8081; /** Porta del server*/
    /** Percorsi dei file */
    public static final String FILE_PATH = "demo/Server/src/main/resources/";
    public static final String USER_PATH = "demo/Server/src/main/resources/users.txt";
    /** Codici operativi richieste */
    public static final int PING = 0;
    public static final int UPDATE_MAILS = 1;
    public static final int SEND_MAIL = 2;
    public static final int LOGIN_CHECK = 3;
    public static final int LOGIN_OK = 4;
    public static final int LOGIN_ERROR = 5;
    public static final int DEST_CHECK = 6;
    public static final int DEST_OK = 7;
    public static final int DEST_ERROR = 8;
    public static final int LOGOUT = 9;
    public static final int DELETE = 10;

    public static final String REQUEST_CODE_KEY = "requestCode";
    public static final String REQUEST_PAYLOAD_KEY = "requestPayload";
    public static final String REQUEST_ID_KEY = "requestId";
    public static final String MAIL_ID_KEY = "mailId";
    public static final String MAIL_SENDER_KEY = "sender";
    public static final String MAIL_TITLE_KEY = "title";
    public static final String MAIL_CONTENT_KEY = "content";
    public static final String MAIL_RECEIVERS_KEY = "receivers";
    public static final String MAIL_DATE_KEY = "date";

    public static final String MAIL_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";



    /**
     * Metodo utilizzato per cambiare scena e ritorna l'istanza del nuovo controller.
     * @param current_stage lo stage corrente.
     * @param prova il FXMLLoader della nuova scena.
     * @param <T> il tipo del controller.
     * @return l'istanza del nuovo controller.
     */

    public static <T> T change_scene(Stage current_stage,  FXMLLoader prova){
        try{
            Scene scene = new Scene(prova.load(), SCENE_WIDTH , SCENE_HEIGHT);
            current_stage.setScene(scene);
            current_stage.show();
            /** Ottieni il controller della scena appena caricata*/
            T controller = prova.getController();

            return controller;

        } catch (IOException e){
            System.out.println("Eccezione catturata durante il cambio di scena");
            return null;
        }
        /**DOPO AVER CAMBIATO SCENA È NECESSARIO "PASSARE" AL NUOVO CONTROLLER IL BACKUP*/
    }

    /**
     * Metodo per verificare se un utente esiste.
     * @param email l'email dell'utente.
     * @return true se l'utente esiste, altrimenti false.
     */

    public static boolean checkUserExists(String email) {
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.USER_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (Exception e) {
            System.out.println("Eccezione catturata durante la verifica dell'esistenza dell'utente");
        }
        return false;
    }

    /**
     * Metodo per generare un intero univoco
     * @param dateTime il tempo corrente
     * @param inputString la stringa di input
     * @return un intero univoco
     */
    public static int generateUniqueInteger(LocalDateTime dateTime, String inputString) {
        // Formattare LocalDateTime in una stringa rappresentativa
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String formattedDateTime = dateTime.format(formatter);

        // Combinare la stringa temporale con la stringa di input
        String combined = formattedDateTime + inputString;

        // Generare un hashcode dal valore combinato
        return combined.hashCode();
    }

    /**
     * Metodo per la verifica della sintassi delle email.
     * @param email l'email da verificare.
     * @return true se l'email è valida, altrimenti false.
     */

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * Costruisce la richiesta e la converte in stringa, per essere inviata al server*/
    public static <T extends Serializable>  String build_request(int requestCode, T payload, String requestId) {
        JSONObject json = new JSONObject();
        json.put(REQUEST_CODE_KEY, requestCode);
        json.put(REQUEST_PAYLOAD_KEY, payload);
        json.put(REQUEST_ID_KEY, requestId);
        return json.toString();
    }

    /**
     * Invia la richiesta*/
    public static boolean sendRequest(Socket clientSocket, String message)  {
        try {
            OutputStream output = clientSocket.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));

            writer.write(message);
            writer.newLine();
            writer.flush();
            return true;
        } catch (Exception e) {
            System.out.println("Errore nell'invio della richiesta ---> " + message + "\n\rCon codice di errore ---> " + e.getMessage());
            return false;
        }
    }

    /**
     * Aspetta la risposta del server e ne fornisce il contenuto*/
    public static JSONObject wait_for_response(Socket clientSocket) {
        try {
            InputStream input = clientSocket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            // Legge l'intera risposta come una singola linea
            String response = reader.readLine();

            // Controlla che la risposta non sia null (es. connessione chiusa)
            if (response == null) {
                throw new Exception("Nessuna risposta dal server.");
            }

            // Converte la stringa in JSONObject
            JSONObject response_json = new JSONObject(response);
            return response_json;
        } catch (Exception e) {
            System.out.println("Errore nel parsing della richiesta dal server");
            return null;
        }
    }

    /**
     * Costruisce un JSONObject mail e lo converte in stringa, per essere inviato al server*/
    public static String build_mail(String sender, String title, String content, String[] receivers, LocalDateTime date, int id) {
        // Crea un nuovo JSONObject
        JSONObject mailJson = new JSONObject();

        // Aggiungi le informazioni della mail
        mailJson.put(Structures.MAIL_SENDER_KEY, sender);
        mailJson.put(Structures.MAIL_TITLE_KEY, title);
        mailJson.put(Structures.MAIL_CONTENT_KEY, content);

        // Converte l'array di receivers in JSONArray
        JSONArray receiversArray = new JSONArray(receivers);
        mailJson.put(Structures.MAIL_RECEIVERS_KEY, receiversArray);

        // Converte la LocalDateTime in una stringa
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Structures.MAIL_TIME_FORMAT);
        String dateString = date.format(formatter);
        mailJson.put(Structures.MAIL_DATE_KEY, dateString);

        // Aggiungi l'id
        mailJson.put(Structures.MAIL_ID_KEY, id);

        // Ritorna la rappresentazione in stringa del JSONObject
        return mailJson.toString();
    }
}