package com.example.server;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

/* Classe controllore per gestire l'interfaccia del server */

public class ServerController {

    @FXML
    public Button stopButton; // Bottone per spegnere il server
    @FXML
    private Button startButton; // Bottone per avviare il server
    @FXML
    private ListView<String> logListView; // ListView per i log

    private ServerManager serverManager; // Gestione separata del server


    /** Inizializza lo stato iniziale dei componenti dell'interfaccia */

    public void initialize() {
        stopButton.setDisable(true);
        stopButton.setVisible(false);
    }

    /** Avvia il server */

    @FXML
    public void startServer() {
        if (serverManager == null) {
            serverManager = new ServerManager(this);
        }
        serverManager.start();

        startButton.setDisable(true);
        startButton.setVisible(false);

        stopButton.setDisable(false);
        stopButton.setVisible(true);
    }

    /** Ferma il server */

    @FXML
    public void stopServer() {
        if (serverManager != null) {
            serverManager.stop();

            startButton.setDisable(false);
            startButton.setVisible(true);

            stopButton.setVisible(false);
            stopButton.setDisable(true);
        }
    }

    /**
     * Aggiunge un messaggio di log alla ListView.
     * @param message Il messaggio di log da aggiungere.
     */

    public void addLog(String message) {
        Platform.runLater(() -> logListView.getItems().add(message));
    }
}