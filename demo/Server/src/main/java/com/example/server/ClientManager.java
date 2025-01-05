// ClientManager.java
package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientManager {

    private Socket clientSocket;
    private final MessageService messageService;
    private final ServerController serverController;
    private final ServerManager servermanager;

    private static final Object lock = new Object();  //condivisa fra tutte le istanze di ClientManager(Ne viene creata 1 per tutte le classi, per questo può essere usata come lock)

    public ClientManager(ServerController serverController, ServerManager serverManager,Socket client_socket) {
        this.messageService = new MessageService(serverManager);
        this.serverController = serverController;
        this.servermanager = serverManager;
        this.clientSocket = client_socket;
    }


    /** Metodo per gestire le richieste del client */
    public void handleClient(JSONObject request) {
        synchronized (lock) {
            try {
                // Switch-case per gestire le richieste
                switch (request.getInt(Structures.REQUEST_CODE_KEY)) {
                    case Structures.UPDATE_MAILS -> handleUpdateMails(request);
                    case Structures.PING -> handlePing(request);
                    case Structures.SEND_MAIL -> handleSendMail(request);
                    case Structures.DEST_CHECK -> handleDestCheck(request);
                    case Structures.LOGOUT -> handleLogout(request);
                    case Structures.DELETE -> handleDelete(request);
                    case Structures.LOGIN_CHECK -> handleLoginCheck(request);
                    default -> {
                        serverController.addLog("Codice richiesta non riconosciuto: " + request.getInt(Structures.REQUEST_CODE_KEY));
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
    }

    /** Metodi per gestire le richieste del client */
    private void handleUpdateMails(JSONObject request) {

        JSONArray existingIds = request.getJSONArray(Structures.REQUEST_PAYLOAD_KEY);
        String newMails_list = messageService.getMessagesByReceiver(request.getString(Structures.REQUEST_ID_KEY), existingIds);
        String new_mails = Structures.build_request(Structures.UPDATE_MAILS, newMails_list, "SERVER");
        if(Structures.sendRequest(clientSocket, new_mails)){
            serverController.addLog("Aggiornamento delle mail inviato a: " + request.getString(Structures.REQUEST_ID_KEY));
        } else {
            serverController.addLog("Errore durante l'aggiornamento delle mail: ");
        }

    }

    /** Metodo per gestire il ping */
    private void handlePing(JSONObject request) {

        String pong_response = Structures.build_request(Structures.PING, "pong", "SERVER");
        if(Structures.sendRequest(clientSocket, pong_response)){
            serverController.addLog("Ping ricevuto da: " + request.getString(Structures.REQUEST_ID_KEY));
        } else {
            serverController.addLog("Errore durante il ping: ");
        }

    }

    /** Metodo per gestire l'invio di una mail */
    private void handleSendMail(JSONObject request) {
        try {
            // Estrai il payload dalla request (la mail inviata)
            String newMailJson_string = request.getString(Structures.REQUEST_PAYLOAD_KEY);
            JSONObject newMailJson = new JSONObject(newMailJson_string);
            JSONArray receivers = newMailJson.getJSONArray(Structures.MAIL_RECEIVERS_KEY);

            // Salva la mail per ogni destinatario
            for (int i = 0; i < receivers.length(); i++) {
                String destinatario = receivers.getString(i);

                // Carica il file esistente, se c'è
                JSONArray existingMails = messageService.loadMessages(destinatario);

                // Aggiungi la nuova mail al JSONArray
                existingMails.put(newMailJson);

                // Scrivi il JSONArray aggiornato nel file
                String filePath = Structures.FILE_PATH + destinatario.split("@")[0] + ".json";
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(existingMails.toString(2)); // scrive con indentazione
                    writer.newLine(); // Aggiungi una nuova linea (opzionale, per una scrittura più ordinata)
                }
                serverController.addLog("Mail salvata con successo per il destinatario: " + destinatario);
            }
        } catch (IOException e) {
            serverController.addLog("Errore durante il salvataggio della mail: " + e.getMessage());
        }
    }

    /** Metodo per gestire la verifica del destinatario */
    private void handleDestCheck(JSONObject request) {

        String destEmail = request.getString(Structures.REQUEST_PAYLOAD_KEY);
        boolean destExists = Structures.checkUserExists(destEmail);
        int responseCode = destExists ? Structures.DEST_OK : Structures.DEST_ERROR;
        String response = Structures.build_request(responseCode, destEmail, "SERVER");
        if (Structures.sendRequest(clientSocket, response)) {
            serverController.addLog("Verifica destinatario per: " + destEmail + " esito: " + (destExists ? "OK" : "ERROR"));
        } else {
            serverController.addLog("Errore durante la verifica del destinatario: ");
        }

    }

    /** Metodo per gestire il logout */
    private void handleLogout(JSONObject request) {
        serverController.addLog("Richiesta di logout ricevuta da ---> " + request.getString(Structures.REQUEST_ID_KEY));
        try {
            clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

    /** Metodo per gestire la verifica del login */
    private boolean handleLoginCheck(JSONObject request) throws IOException {
        String userEmail = request.getString(Structures.REQUEST_ID_KEY);
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

        String response = Structures.build_request(responseCode, userEmail, "SERVER");
        if (Structures.sendRequest(clientSocket, response)) {
            serverController.addLog("Verifica login per " + userEmail + " esito: " + (response_bool ? "OK" : "ERROR"));
        } else {
            serverController.addLog("Errore durante la verifica del login per: " + userEmail);
        }
        return response_bool;
    }

    /** Metodo per gestire la cancellazione delle mail */
    private void handleDelete(JSONObject request) {

        String email = request.getString(Structures.REQUEST_ID_KEY);

        // Carica le mail per l'email specificata usando la funzione loadMessages
        JSONArray allMails = messageService.loadMessages(email);

        // Ottieni gli ID delle mail da eliminare dal campo "payload"
        JSONArray mailIdsToDelete = request.getJSONArray(Structures.REQUEST_PAYLOAD_KEY);

        try {
            // Filtra le mail da mantenere (rimuovendo quelle con id presente in mailIdsToDelete)
            JSONArray filteredMails = new JSONArray();
            for (int i = 0; i < allMails.length(); i++) {
                JSONObject mail = allMails.getJSONObject(i);
                int mailId = mail.getInt(Structures.MAIL_ID_KEY);  // Ottieni l'ID della mail

                // Aggiungi la mail all'array filtrato solo se il suo ID non è nell'elenco di ID da eliminare
                boolean shouldDelete = false;
                for (int j = 0; j < mailIdsToDelete.length(); j++) {
                    if (mailIdsToDelete.getInt(j) == mailId) {
                        shouldDelete = true;
                        break;
                    }
                }

                // Se non deve essere eliminata, aggiungila all'array filtrato
                if (!shouldDelete) {
                    filteredMails.put(mail);
                }
            }

            // Scrivi il nuovo JSONArray nel file (solo le mail da mantenere)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(Structures.FILE_PATH + email.split("@")[0] + ".json"))) {
                writer.write(filteredMails.toString(2)); // Scrive con indentazione
            }

            serverController.addLog("Mail eliminate per: " + email);

        } catch (IOException e) {
            serverController.addLog("Errore durante la cancellazione delle mail per " + email + ": " + e.getMessage());
        } catch (JSONException e) {
            serverController.addLog("Errore nel parsing del JSON: " + e.getMessage());
        }
    }

    /** Metodo per chiudere la connessione */
    private void closeConnection() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

}