package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import com.example.shared.Request;
import java.io.*;
import java.net.Socket;
import java.util.*;

/**
 * Classe per gestire le connessioni dei client e le loro richieste in arrivo.
 */

public class ClientManager {

    private Socket clientSocket;
    private final MessageService messageService;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private final ServerController serverController;
    private final ServerManager servermanager;
    private String email; // Email del client

    /**
     * Costruttore della classe ClientManager.
     * @param serverController Il controller del server.
     * @param serverManager Il manager del server.
     * @param output Lo stream di output per il client.
     * @param input Lo stream di input per il client.
     * @param clientSocket Il socket del client.
     */

    public ClientManager(ServerController serverController, ServerManager serverManager, ObjectOutputStream output, ObjectInputStream input, Socket clientSocket) {
        this.messageService = new MessageService(serverManager);
        this.output = output;
        this.input = input;
        this.serverController = serverController;
        this.servermanager = serverManager;
        this.clientSocket = clientSocket;
    }

    /** Restituisce l'email del client
     @return email del client
     */
    public String getEmail() {
        return email;
    }

    /** Imposta l'email del client
     @param email email del client
     */

    public void setEmail(String email) {
        this.email = email;
    }

    /** Imposta il socket del client
     @param socket socket del client
     */

    public void set_socket(Socket socket) {
        this.clientSocket = socket;
    }

    /** Gestisce le richieste in arrivo dal client.
     * @param request La richiesta del client.
     */

    public void handleClient(Request<?> request) {
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

    /** Gestisce le richieste di logout del client */

    private void handleLogout() {
        // Aggiorna il file_pointer del client che ha fatto logout
        servermanager.updateClientFilePointer(this.getEmail(), 0);
        serverController.addLog("Logout request received, resetting file pointer.");
    }

    /**
     * Gestisce la richiesta di eliminazione delle email.
     * @param request La richiesta di eliminazione delle email.
     * @throws IOException Se si verifica un errore durante l'eliminazione delle email.
     */

    private void handleDelete(Request<ArrayList<Mail>> request) throws IOException {
        /** Converto in un array di stringhe */
        String[] mailStrings = new String[request.getPayload().size()];
        for (int i = 0; i < request.getPayload().size(); i++) {
            mailStrings[i] = request.getPayload().get(i).toString(); // Chiama toString() su ogni Mail
        }

        /** Ottieni l'elenco di email da eliminare come un Set per confronti più rapidi */
        Set<String> emailsToDelete = new HashSet<>(Arrays.asList(mailStrings));

        /** File da modificare */
        File file = new File(Structures.FILE_PATH);

        if (!file.exists()) {
            serverController.addLog("Il file non esiste: " + Structures.FILE_PATH);
            return;
        }
        /** Legge tutto il contenuto del file in memoria e filtra le righe da eliminare */
        List<String> filteredLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!emailsToDelete.contains(trimmedLine)) {
                    filteredLines.add(line); /** Aggiunge solo le righe che non devono essere eliminate */
                }
            }
        }
        /** Scrive nuovamente il contenuto filtrato nel file, sovrascrivendo il vecchio contenuto */
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : filteredLines) {
                writer.write(line);
                writer.newLine(); /** Scrive ogni riga con una nuova linea */
            }
        }

        /** Aggiorna il file_pointer del client che ha inviato l'email */
        int newPointer = servermanager.getClientFilePointer(this.getEmail()) - mailStrings.length;
        servermanager.updateClientFilePointer(this.getEmail(), newPointer);

        serverController.addLog("Email deletion success:\n" + emailsToDelete + "\n----> DELETED");
    }

    /**
     * Gestisce la richiesta di aggiornamento delle email.
     * @param request_with_mail La richiesta di aggiornamento delle email.
     * @throws IOException Se si verifica un errore durante l'aggiornamento delle email.
     */

    private void handleUpdateMails(Request<String> request_with_mail) throws IOException {
        ArrayList<Mail> messages = messageService.getMessagesByReceiver(request_with_mail.getPayload(), this.getEmail());
        Request<ArrayList<Mail>> response = new Request<>(Structures.UPDATE_MAILS, messages, "SERVER");
        sendResponse(response);
        serverController.addLog("Email inviate con successo!");
    }

    /**
     * Gestisce la richiesta di ping.
     * @throws IOException Se si verifica un errore durante la gestione del ping.
     */

    private void handlePing() throws IOException {
        Request<String> response = new Request<>(Structures.PING, "pong", "SERVER");
        sendResponse(response);
        serverController.addLog("Ping gestito correttamente!");
    }

    /**
     * Gestisce la richiesta di invio email.
     * @param request La richiesta di invio email.
     */

    private void handleSendMail(Request<Mail> request) {
        try {
            Mail mail = request.getPayload();
            saveMessage(mail);
            serverController.addLog("Messaggio salvato: " + mail);
        } catch (IOException e) {
            serverController.addLog("Errore durante il salvataggio del messaggio: " + e.getMessage());
        }
    }

    /**
     * Legge i dati dal client.
     * @return La richiesta del client.
     * @throws IOException Se si verifica un errore durante la lettura dei dati.
     * @throws ClassNotFoundException Se la classe della richiesta non viene trovata.
     */

    private Request<?> readDataFromClient() throws IOException, ClassNotFoundException {
        return (Request<?>) input.readObject();
    }

    /**
     * Invia una risposta al client.
     * @param response La risposta da inviare.
     * @throws IOException Se si verifica un errore durante l'invio della risposta.
     */

    private void sendResponse(Request<?> response) throws IOException {
        output.writeObject(response);
        output.flush();
    }

    /**
     * Salva un messaggio nel file.
     * @param message Il messaggio da salvare.
     * @throws IOException Se si verifica un errore durante il salvataggio del messaggio.
     */

    private void saveMessage(Mail message) throws IOException {
        File file = new File(Structures.FILE_PATH);

        /** Crea il file se non esiste */
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Impossibile creare il file: " + Structures.FILE_PATH);
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Structures.FILE_PATH, true))) {
            if(file.length() != 0) {
                writer.write("\n");
            }
            writer.write(message.toString());
        }
    }

    /**
     * Gestisce la richiesta di verifica del destinatario.
     * @param request La richiesta di verifica del destinatario.
     * @throws IOException Se si verifica un errore durante la verifica del destinatario.
     */

    private void handleDestCheck(Request<String> request) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = Structures.checkUserExists(userEmail);
        int responseCode;
        if (userExists) {
            serverController.addLog("User " + userEmail + " found. Sending DEST_OK.");
            responseCode = Structures.DEST_OK;
        } else {
            serverController.addLog("User " + userEmail + " not found. Sending DEST_ERROR.");
            responseCode = Structures.DEST_ERROR;
        }

        Request<String> response = new Request<>(responseCode, userEmail, "SERVER");
        sendResponse(response);
    }

    /** Chiude la connessione con il client */

    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            clientSocket.close();
        } catch (IOException e) {
            serverController.addLog("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}