package com.example.server;

import com.example.shared.Structures;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Classe principale che gestisce l'interfaccia grafica e avvia il server.
 */
public class ServerController {

    @FXML
    public Button stopButton; // Bottone per spegnere il server
    @FXML
    private Button startButton; // Bottone per avviare il server

    private ServerManager serverManager; // Gestione separata del server

    /**
     * Avvia il server in background e aggiorna l'interfaccia.
     */

    public void initialize() {
        // Rende il bottone di spegnimento inizialmente non visibile
        stopButton.setDisable(true);
        stopButton.setVisible(false);
    }

    @FXML
    public void startServer() {
        if (serverManager == null) {
            serverManager = new ServerManager();
        }
        serverManager.start();

        // Tolgo il bottone di start e rendo visibile il bottone di stop
        startButton.setDisable(true);
        startButton.setVisible(false);

        stopButton.setDisable(false);
        stopButton.setVisible(true);

    }

    /**
     * Arresta il server
     */
    public void stopServer() {
        if (serverManager != null) {
            serverManager.stop();

            //Rendo visibile il bottone start e invisibile il bottone stop
            startButton.setDisable(false);
            startButton.setVisible(true);

            stopButton.setVisible(false);
            stopButton.setDisable(true);
        }
    }
}