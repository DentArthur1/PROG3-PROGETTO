// ClientManager.java
package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import com.example.shared.Request;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientManager {

    private Socket clientSocket;
    private final MessageService messageService;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final ServerController serverController;
    private final ServerManager servermanager;

    public ClientManager(ServerController serverController, ServerManager serverManager) {
        this.messageService = new MessageService(serverManager);
        this.serverController = serverController;
        this.servermanager = serverManager;
    }


    /** Getter e setter per il socket del client (per la chiusura della connessione) */
    public void set_socket(Socket socket) {
        this.clientSocket = socket;
    }

    public void set_input(ObjectInputStream input) {
        this.input = input;
    }

    public void set_output(ObjectOutputStream output) {
        this.output = output;
    }

    /** Metodo per gestire le richieste del client */
    public void handleClient(Request<?> request) {
        try {
            // Switch-case per gestire le richieste
            switch (request.getRequestCode()) {
                case Structures.UPDATE_MAILS -> handleUpdateMails((Request<List<Integer>>) request);
                case Structures.PING -> handlePing((Request<String>) request);
                case Structures.SEND_MAIL -> handleSendMail((Request<Mail>) request);
                case Structures.DEST_CHECK -> handleDestCheck((Request<String>) request);
                case Structures.LOGOUT -> handleLogout();
                case Structures.DELETE -> handleDelete((Request<ArrayList<Integer>>) request);
                case Structures.LOGIN_CHECK -> handleLoginCheck((Request<String>) request, output);
                default -> {
                    serverController.addLog("Codice richiesta non riconosciuto: " + request.getRequestCode());
                    throw new Exception("Codice richiesta sconosciuto");
                }
            }

        } catch (Exception e) {
            serverController.addLog("Errore nella gestione del client: ");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /** Metodi per gestire le richieste del client */
    private synchronized void handleUpdateMails(Request<List<Integer>> request) {
        try {
            List<Integer> existingIds = request.getPayload();
            ArrayList<Mail> newMails = messageService.getMessagesByReceiver(request.getRequestId(), existingIds);
            Request<ArrayList<Mail>> response = new Request<>(Structures.UPDATE_MAILS, newMails, "SERVER");
            output.writeObject(response);
            serverController.addLog("Aggiornamento delle mail inviato a: " + request.getRequestId());
        } catch (IOException e) {
            serverController.addLog("Errore durante l'aggiornamento delle mail: " + e.getMessage());
        }
    }

    /** Metodo per gestire il ping */
    private void handlePing(Request<String> request) {
        try {
            Request<String> response = new Request<>(Structures.PING, "pong", "SERVER");
            output.writeObject(response);
            serverController.addLog("Ping ricevuto da: " + request.getRequestId());
        } catch (IOException e) {
            serverController.addLog("Errore durante il ping: " + e.getMessage());
        }
    }

    /** Metodo per gestire l'invio di una mail */
    private synchronized void handleSendMail(Request<Mail> request) {
        try {
            Mail newMail = request.getPayload();
            serverController.addLog("Nuova mail ricevuta da: " + newMail.getSender());
            for (String destinatario : newMail.getReceivers()) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(Structures.FILE_PATH + destinatario.split("@")[0] + ".txt", true))) {
                    writer.write(newMail.toString());
                    writer.newLine();
                }
                serverController.addLog("Mail salvata con successo.");
            }
        } catch (IOException e) {
            serverController.addLog("Errore durante il salvataggio della mail: " + e.getMessage());
        }
    }

    /** Metodo per gestire la verifica del destinatario */
    private synchronized void handleDestCheck(Request<String> request) {
        try {
            String destEmail = request.getPayload();
            boolean destExists = Structures.checkUserExists(destEmail);
            int responseCode = destExists ? Structures.DEST_OK : Structures.DEST_ERROR;
            Request<String> response = new Request<>(responseCode, destEmail, "SERVER");
            output.writeObject(response);
            serverController.addLog("Verifica destinatario per: " + destEmail + " esito: " + (destExists ? "OK" : "ERROR"));
        } catch (IOException e) {
            serverController.addLog("Errore durante la verifica del destinatario: " + e.getMessage());
        }
    }

    /** Metodo per gestire il logout */
    private synchronized void handleLogout() {
        serverController.addLog("Logout request received, resetting file pointer.");
        try {
            if (output != null) output.close();
            clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

    /** Metodo per gestire la verifica del login */
    private synchronized boolean handleLoginCheck(Request<String> request, ObjectOutputStream output) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = Structures.checkUserExists(userEmail);
        int responseCode;
        boolean response_bool;
        if (userExists) {
            serverController.addLog("User " + userEmail + " trovato. Invio LOGIN_OK.");
            responseCode = Structures.LOGIN_OK;
            response_bool = true;
        } else {
            serverController.addLog("User " + userEmail + " non trovato. Invio LOGIN_ERROR.");
            responseCode = Structures.LOGIN_ERROR;
            response_bool = false;
        }

        Request<String> response = new Request<>(responseCode, userEmail, "SERVER");
        output.writeObject(response);
        return response_bool;
    }

    /** Metodo per gestire la cancellazione delle mail */
    private synchronized void handleDelete(Request<ArrayList<Integer>> request) {

        String filePath = Structures.FILE_PATH + request.getRequestId().split("@")[0] + ".txt";
        ArrayList<Integer> mailIdsToDelete = request.getPayload();

        try {
            // Leggi tutte le righe dal file
            List<String> allLines = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    allLines.add(line);
                }
            }

            // Filtra le righe da mantenere
            List<String> filteredLines = new ArrayList<>();
            for (String line : allLines) {
                // Split della riga per ottenere l'ID
                String[] parts = line.split("§");
                if (parts.length > 0) {
                    try {
                        int id = Integer.parseInt(parts[parts.length - 1]); // Ultimo elemento è l'ID
                        if (!mailIdsToDelete.contains(id)) {
                            filteredLines.add(line); // Mantieni la riga se l'ID non è nell'elenco
                        }
                    } catch (NumberFormatException e) {
                        // Se l'ID non è un numero, mantieni comunque la riga
                        filteredLines.add(line);
                    }
                }
            }

            // Scrivi le righe filtrate nel file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String filteredLine : filteredLines) {
                    writer.write(filteredLine);
                    writer.newLine();
                }
            }

            serverController.addLog("Mail eliminate per: " + request.getRequestId());
        } catch (IOException e) {
            serverController.addLog("Errore durante la cancellazione delle mail per " + request.getRequestId() + ": " + e.getMessage());
        }
    }

    /** Metodo per chiudere la connessione */
    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}