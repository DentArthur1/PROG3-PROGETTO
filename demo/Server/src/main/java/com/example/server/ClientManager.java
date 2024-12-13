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
    private String email; // Email del client

    public ClientManager(ServerController serverController, ServerManager serverManager, ObjectOutputStream output, ObjectInputStream input, Socket clientSocket) {
        this.messageService = new MessageService(serverManager);
        this.output = output;
        this.input = input;
        this.serverController = serverController;
        this.servermanager = serverManager;
        this.clientSocket = clientSocket;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void set_socket(Socket socket) {
        this.clientSocket = socket;
    }

    public synchronized void handleClient(Request<?> request) {
        try {
            // Switch-case per gestire le richieste
            setEmail(request.getAuthToken());
            switch (request.getRequestCode()) {
                case Structures.UPDATE_MAILS -> handleUpdateMails((Request<String>) request);
                case Structures.PING -> handlePing();
                case Structures.SEND_MAIL -> handleSendMail((Request<Mail>) request);
                case Structures.DEST_CHECK -> handleDestCheck((Request<String>) request);
                case Structures.LOGOUT -> handleLogout();
                case Structures.DELETE -> handleDelete((Request<ArrayList<Mail>>) request);
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

    private synchronized void handleUpdateMails(Request<String> request) {
        try {
            ArrayList<Mail> newMails = messageService.getMessagesByReceiver(request.getPayload(), email);
            Request<ArrayList<Mail>> response = new Request<>(Structures.UPDATE_MAILS, newMails, email, request.getLastMailId());
            output.writeObject(response);
            serverController.addLog("Aggiornamento delle mail inviato a: " + email);
        } catch (IOException e) {
            serverController.addLog("Errore durante l'aggiornamento delle mail: " + e.getMessage());
        }
    }

    private synchronized void handlePing() {
        try {
            Request<String> response = new Request<>(Structures.PING, "pong", email, 0);
            output.writeObject(response);
            serverController.addLog("Ping ricevuto da: " + email);
        } catch (IOException e) {
            serverController.addLog("Errore durante il ping: " + e.getMessage());
        }
    }

    private synchronized void handleSendMail(Request<Mail> request) {
        try {
            Mail newMail = request.getPayload();
            serverController.addLog("Nuova mail ricevuta da: " + newMail.getSender());
            // Scrivi la mail nel file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(Structures.FILE_PATH, true))) {
                writer.write(newMail.toString());
                writer.newLine();
            }
            serverController.addLog("Mail salvata con successo.");
        } catch (IOException e) {
            serverController.addLog("Errore durante il salvataggio della mail: " + e.getMessage());
        }
    }

    private synchronized void handleDestCheck(Request<String> request) {
        try {
            String destEmail = request.getPayload();
            boolean destExists = Structures.checkUserExists(destEmail);
            int responseCode = destExists ? Structures.DEST_OK : Structures.DEST_ERROR;
            Request<String> response = new Request<>(responseCode, destEmail, email, request.getLastMailId());
            output.writeObject(response);
            serverController.addLog("Verifica destinatario per: " + destEmail + " esito: " + (destExists ? "OK" : "ERROR"));
        } catch (IOException e) {
            serverController.addLog("Errore durante la verifica del destinatario: " + e.getMessage());
        }
    }

    private synchronized void handleLogout() {
        // Aggiorna il file_pointer del client che ha fatto logout
        servermanager.updateClientFilePointer(this.getEmail(), 0);
        serverController.addLog("Logout request received, resetting file pointer.");
        try {
            if (output != null) output.close();
            clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

    private synchronized void handleDelete(Request<ArrayList<Mail>> request) {
        try {
            ArrayList<Mail> mailsToDelete = request.getPayload();
            ArrayList<Mail> allMails = messageService.loadAllMailfromUser();

            for (Mail mail : mailsToDelete) {
                for (Mail existingMail : allMails) {
                    if (existingMail.equals(mail)) {
                        List<String> dest = new ArrayList<>(Arrays.asList(existingMail.getReceivers()));
                        if (dest.size() > 1) {
                            dest.remove(email);
                            existingMail.setReceivers(dest.toArray(new String[0]));
                            existingMail.setModified(true); // Imposta il campo modified a true
                        } else {
                            allMails.remove(existingMail);
                        }
                        break;
                    }
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(Structures.FILE_PATH))) {
                for (Mail mail : allMails) {
                    writer.write(mail.toString());
                    writer.newLine();
                }
            }
            serverController.addLog("Mail cancellata per: " + email);
        } catch (IOException e) {
            serverController.addLog("Errore durante la cancellazione della mail: " + e.getMessage());
        }
    }

    private synchronized void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}