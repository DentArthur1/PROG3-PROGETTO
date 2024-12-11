package com.example.shared;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

public class Structures {
    /**
     * Classe di supporto per la definizione di costanti, import e funzioni
     * @param EMAIL_REGEX: espressione regolare per la validazione delle email, prende solo domini con almeno 2 caratteri e con un solo punto
     * @param PORT: porta del server
     */
    public static final int SCENE_WIDTH = 500;
    public static final int SCENE_HEIGHT = 500;
    public static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    public static final int PORT = 8081; /** Porta del server*/
    public static final String FILE_PATH = "demo/Server/src/main/resources/data.txt";
    public static final String USER_PATH = "demo/Server/src/main/resources/users.txt";
    //Codici operativi richieste
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

    public static <T> T change_scene(Stage current_stage,  FXMLLoader prova){
        /**Metodo utilizzato per cambiare scena e ritorna l'istanza del nuovo controller*/
        try{
            Scene scene = new Scene(prova.load(), SCENE_WIDTH , SCENE_HEIGHT);
            current_stage.setScene(scene);
            current_stage.show();
            /** Ottieni il controller della scena appena caricata*/
            T controller = prova.getController();

            return controller;

        } catch (IOException e){
            System.out.println("Exeption caught while changing scene");
            return null;
        }
        /**DOPO AVER CAMBIATO SCENA È NECESSARIO "PASSARE" AL NUOVO CONTROLLER IL BACKUP*/
    }


    public static boolean isValidEmail(String email) {
        /**Metodo per la verifica della sintassi delle mail*/
        return Pattern.matches(EMAIL_REGEX, email);
    }


}