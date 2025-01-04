// ServerManager.java
package com.example.server;

import com.example.shared.Request;
import com.example.shared.Structures;

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

    public synchronized void start() {
        if (running) {
            serverController.addLog("The server is already running.");
            return;
        }
        running = true;
        new Thread(this).start();
        serverController.addLog("Server attivo.");
    }

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
            serverController.addLog("Error stopping the server: " + e.getMessage());
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
                    Socket clientSocket = serverSocket.accept();
                    serverController.addLog("Connessione ricevuta da: " + clientSocket.getInetAddress());

                    new Thread(() -> {
                        try {
                            ClientManager clientManager = new ClientManager(serverController, this, clientSocket);
                            clientManager.handleClient();
                        } catch (Exception e) {
                            serverController.addLog("Authentication process failed");
                        }
                    }).start();

                } catch (IOException e) {
                    if (running) {
                        serverController.addLog("Error accepting client connection: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            serverController.addLog("Error running the server: " + e.getMessage());
        } finally {
            if (serverSocket != null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    serverController.addLog("Error closing the server socket: " + e.getMessage());
                }
            }
        }
    }
}