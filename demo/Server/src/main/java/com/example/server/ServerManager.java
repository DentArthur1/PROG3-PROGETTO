package com.example.server;

import com.example.shared.Structures;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerManager implements Runnable {

    private volatile boolean running = false; // Flag per controllare lo stato del server
    private ServerSocket serverSocket;

    /**
     * Avvia il server.
     */
    public synchronized void start() {
        if (running) {
            System.out.println("Il server è già in esecuzione.");
            return;
        }
        running = true;
        new Thread(this).start(); // Avvia il thread con il metodo `run`
        System.out.println("Server avviato.");
    }

    /**
     * Ferma il server.
     */
    public synchronized void stop() {
        if (!running) {
            System.out.println("Il server non è in esecuzione.");
            return;
        }
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // Chiude il server socket per interrompere l'attesa su `accept`
            }
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura del server socket: " + e.getMessage());
        }
        System.out.println("Server fermato.");
    }

    /**
     * Metodo principale del server, chiamato quando viene avviato il thread.
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            /**
             * SOLUZIONE NON MULTI-THREAD */
            this.serverSocket = serverSocket;
            System.out.println("Server in ascolto sulla porta " + Structures.PORT);
            ClientManager clientManager = new ClientManager();
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Crea un ClientManager per gestire il client
                    clientManager.set_socket(clientSocket);
                    clientManager.handleClient(); // Gestisce il client
                } catch (IOException e) {
                    if (running) { // Se il server è stato fermato, ignora l'errore
                        System.err.println("Errore nella connessione al client: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'esecuzione del server: " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    /**
     * Pulisce le risorse dopo la chiusura del server.
     */
    private void cleanup() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la pulizia del server socket: " + e.getMessage());
        }
    }
}