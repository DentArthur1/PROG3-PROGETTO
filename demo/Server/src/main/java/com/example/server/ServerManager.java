package com.example.server;

import com.example.shared.Structures;
import org.json.JSONObject;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager implements Runnable {
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private final ServerController serverController;


    public ServerManager(ServerController serverController) {
        this.serverController = serverController;
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

                    new Thread(() -> {
                        try {
                            ClientManager clientManager = new ClientManager(serverController, this, clientSocket);
                            //Parsing della richiesta in un JSONOBJECT
                            JSONObject generic_request = Structures.wait_for_response(clientSocket);
                            //Passaggio a clientmanager
                            clientManager.handleClient(generic_request);
                        } catch (Exception e) {
                            serverController.addLog("Authentication process failed");
                        }
                    }).start();

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