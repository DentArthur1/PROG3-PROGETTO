package com.example.server;

import com.example.shared.Structures;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager implements Runnable {

    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private final ServerController serverController;

    public ServerManager(ServerController serverController) {
        this.serverController = serverController;
    }

    public synchronized void start() {
        if (running) {
            serverController.addLog("Il server è già in esecuzione.");
            return;
        }
        running = true;
        new Thread(this).start();
        serverController.addLog("Server avviato.");
    }

    public synchronized void stop() {
        if (!running) {
            serverController.addLog("Il server non è in esecuzione.");
            return;
        }
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura del server socket: " + e.getMessage());
        }
        serverController.addLog("Server fermato.");
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            this.serverSocket = serverSocket;
            serverController.addLog("Server in ascolto sulla porta " + Structures.PORT);
            ClientManager clientManager = new ClientManager(serverController);
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    clientManager.set_socket(clientSocket);
                    clientManager.handleClient();
                } catch (IOException e) {
                    if (running) {
                        serverController.addLog("Errore nella connessione al client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            serverController.addLog("Errore durante l'esecuzione del server: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void cleanup() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Errore durante la pulizia del server socket: " + e.getMessage());
        }
    }
}