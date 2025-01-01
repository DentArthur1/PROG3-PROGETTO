package com.example.server;

import com.example.shared.Request;
import com.example.shared.Structures;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ServerManager implements Runnable {
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private final ServerController serverController;
    private final ClientManager clientManager;

    public ServerManager(ServerController serverController) {
        this.serverController = serverController;
        // crea un nuovo client manager (FUORI DAL LOOP, IN MODO DA CREARE SOLO 1 CLASSE CLIENTMANAGER E ABILITARE LA SINCRONIZZAZIONE PER I METODI CHE AGISCONO SUI FILE)
        clientManager = new ClientManager(serverController, this);
    }

    /** Avvia il server (thread) */
    public synchronized void start() {
        if (running) {
            serverController.addLog("The server is already running.");
            return;
        }
        running = true;
        new Thread(this).start();  // Start the server thread
        serverController.addLog("Server attivo.");
    }

    /** Ferma il server (socket) */
    public synchronized void stop() {
        if (!running) {
            serverController.addLog("The server is not running.");
            return;
        }
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Error closing the server socket: " + e.getMessage());
        }
        serverController.addLog("Server fermato.");
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            this.serverSocket = serverSocket;
            serverController.addLog("Il server ascolta sulla porta: " + Structures.PORT);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Aspetta una connessione
                    serverController.addLog("Connessione ricevuta da: " + clientSocket.getInetAddress());

                    // Legge la richiesta del client e crea un ClientManager per gestirla
                    try {
                        //In base alla connessione ricevuta viene riconfigurata la stessa classe ClientManager
                        //Tutti i client interagiscono con la stessa classe ClientManager
                        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                        clientManager.set_input(input);
                        clientManager.set_output(output);
                        clientManager.set_socket(clientSocket);
                        Request<?> generic_request = (Request<?>) input.readObject();
                        //chiamo handle_client per il client specifico
                        clientManager.handleClient(generic_request);

                    } catch (Exception e) {
                        serverController.addLog("Authentication process failed");
                    }

                } catch (IOException e) {
                    if (running) {
                        serverController.addLog("Error connecting to client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            serverController.addLog("Error running the server: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /** Chiude il server socket */
    private void cleanup() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Error during server socket cleanup: " + e.getMessage());
        }
    }
}