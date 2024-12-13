package com.example.server;

import com.example.shared.Request;
import com.example.shared.Structures;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/** Classe per gestire il server e le connessioni dei client */

public class ServerManager implements Runnable {
    private volatile boolean running = false;
    private ServerSocket serverSocket;
    private final ServerController serverController;
    private String new_user_email;

    /** Mappa per tenere traccia dei file pointers per ogni client */
    private final Map<String, Integer> clientFilePointers = new HashMap<>();

    /**
     * Costruttore della classe ServerManager.
     * @param serverController Il controller del server.
     */

    public ServerManager(ServerController serverController) {
        this.serverController = serverController;
    }

    public synchronized void start() {
        if (running) {
            serverController.addLog("Il server è già in esecuzione.");
            return;
        }
        running = true;
        new Thread(this).start();  // Avvia il thread del server
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

    /** Metodo run del thread del server */

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(Structures.PORT)) {
            this.serverSocket = serverSocket;
            serverController.addLog("Server in ascolto sulla porta " + Structures.PORT);

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept(); // Aspetta un nuovo client
                    serverController.addLog("Connessione ricevuta da: " + clientSocket.getInetAddress());
                    //INSERIRE POSSIBILE THREAD QUI

                    //leggo l'autenticazione del client //OCCHIO A NON AGGIUNGERE MAIL NON VALIDE, FAI PRIMA IL CONTROLLO
                    try {
                        ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
                        ObjectOutputStream output = new ObjectOutputStream(clientSocket.getOutputStream());
                        Request<?> generic_request = (Request<?>) input.readObject();

                        // Crea un nuovo ClientManager per il client
                        ClientManager clientManager = new ClientManager(serverController, this,output, input, clientSocket);
                        clientManager.set_socket(clientSocket);

                        if (generic_request != null && generic_request.getRequestCode() == Structures.LOGIN_CHECK) { // IL CLIENT STA ENTRANDO PER LA PRIMA VOLTA
                                 if(handleLoginCheck((Request<String>) generic_request, output)){
                                     //Aggiungo nuova persona
                                     String new_email = ((Request<String>) generic_request).getPayload();
                                     clientFilePointers.put(new_email, 0);
                                     serverController.addLog("Cliente: " + new_email + " incontrato per la prima volta, aggiungo il suo autenticatore");
                                 }
                        } else if (clientFilePointers.containsKey(generic_request.getAuthToken())) { //RICHIESTA GENERICA, RECUPERO L'AUTENTICAZIONE
                                String client_email = generic_request.getAuthToken();
                                serverController.addLog("File pointer recuperato per il client: " + client_email + "Con valore: " + clientFilePointers.get(generic_request.getAuthToken()));
                                clientManager.handleClient(generic_request);
                        } else {
                            throw new Exception("Client sconosciuto");
                        }

                    } catch (Exception e) {
                        serverController.addLog("Fail nel processo di autenticazione");
                    }

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

/** Metodo per pulire le risorse del server */

    private void cleanup() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            serverController.addLog("Errore durante la pulizia del server socket: " + e.getMessage());
        }
    }

    /**
     * Restituisce il file pointer del client.
     * @param email L'email del client.
     * @return Il file pointer del client.
     */

    public synchronized int getClientFilePointer(String email) {
        return clientFilePointers.get(email);
    }

    /**
     * Aggiorna il file pointer del client.
     * @param email L'email del client.
     * @param filePointer Il nuovo file pointer.
     */

    public synchronized void updateClientFilePointer(String email, int filePointer) {
        clientFilePointers.put(email, filePointer);
        serverController.addLog("Aggiorno il file pointer di: " + email + " a: " + filePointer);
    }

    /**
     * Gestisce la verifica del login.
     * @param request La richiesta di login.
     * @param output Lo stream di output per il client.
     * @return true se l'utente esiste, false altrimenti.
     * @throws IOException Se si verifica un errore durante la verifica del login.
     */

    private boolean handleLoginCheck(Request<String> request, ObjectOutputStream output) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = Structures.checkUserExists(userEmail);
        int responseCode; boolean response_bool;
        if (userExists) {
            serverController.addLog("User " + userEmail + " found. Sending LOGIN_OK.");
            responseCode = Structures.LOGIN_OK;
            response_bool = true;
        } else {
            serverController.addLog("User " + userEmail + " not found. Sending LOGIN_ERROR.");
            responseCode = Structures.LOGIN_ERROR;
            response_bool = false;
        }

        Request<String> response = new Request<>(responseCode, userEmail, "SERVER");
        output.writeObject(response);
        return response_bool;
    }


}
