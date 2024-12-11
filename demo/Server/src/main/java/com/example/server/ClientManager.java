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

    public ClientManager(ServerController serverController) {
        this.messageService = new MessageService();
        this.serverController = serverController;
    }

    public void set_socket(Socket socket) {
        this.clientSocket = socket;
    }

    public void handleClient() {
        try {
            // Inizializza gli stream all'inizio
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush(); // Invia l'header
            input = new ObjectInputStream(clientSocket.getInputStream());

            Request<?> request = readDataFromClient();

            // Switch-case per gestire le richieste
            switch (request.getRequestCode()) {
                case Structures.UPDATE_MAILS -> handleUpdateMails((Request<String>) request);
                case Structures.PING -> handlePing();
                case Structures.SEND_MAIL -> handleSendMail((Request<Mail>) request);
                case Structures.LOGIN_CHECK -> handleLoginCheck((Request<String>) request);
                case Structures.DEST_CHECK -> handleDestCheck((Request<String>) request);
                case Structures.LOGOUT -> handleLogout();
                case Structures.DELETE -> handleDelete((Request<ArrayList<Mail>>) request);
                default -> serverController.addLog("Codice richiesta non riconosciuto: " + request.getRequestCode());
            }

        } catch (IOException | ClassNotFoundException e) {
            serverController.addLog("Errore nella gestione del client: ");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    private void handleLogout() {
        messageService.reset_file_pointer();
        serverController.addLog("Logout request received, resetting file pointer.");
    }
    /**
     * ELIMINA LE MAIL DAL SERVER*/
    private void handleDelete(Request<ArrayList<Mail>> request) throws IOException {
        // Converto in un array di stringhe
        String[] mailStrings = new String[request.getPayload().size()];
        for (int i = 0; i < request.getPayload().size(); i++) {
            mailStrings[i] = request.getPayload().get(i).toString(); // Chiama toString() su ogni Mail
        }

        // Ottieni l'elenco di email da eliminare come un Set per confronti più rapidi
        Set<String> emailsToDelete = new HashSet<>(Arrays.asList(mailStrings));

        // File da modificare
        File file = new File(Structures.FILE_PATH);

        if (!file.exists()) {
            serverController.addLog("Il file non esiste: " + Structures.FILE_PATH);
            return;
        }
        // Leggi tutto il contenuto del file in memoria e filtra le righe da eliminare
        List<String> filteredLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!emailsToDelete.contains(trimmedLine)) {
                    filteredLines.add(line); // Aggiungi solo le righe che non devono essere eliminate
                }
            }
        }
        // Scrivi nuovamente il contenuto filtrato nel file, sovrascrivendo il vecchio contenuto
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : filteredLines) {
                writer.write(line);
                writer.newLine(); // Scrivi ogni riga con una nuova linea
            }
        }
        // Aggiorno il riferimento al puntatore (nel tuo contesto)
        this.messageService.decrease_file_pointer(mailStrings.length);

        serverController.addLog("Email deletion success:\n" + emailsToDelete + "\n----> DELETED");
    }



    private void handleUpdateMails(Request<String> request_with_mail) throws IOException {
        ArrayList<Mail> messages = messageService.getMessagesByReceiver(request_with_mail.getPayload());
        Request<ArrayList<Mail>> response = new Request<>(Structures.UPDATE_MAILS, messages);
        sendResponse(response);
        serverController.addLog("Email inviate con successo!");
    }

    private void handlePing() throws IOException {
        Request<String> response = new Request<>(Structures.PING, "pong");
        sendResponse(response);
        serverController.addLog("Ping gestito correttamente!");
    }

    private void handleSendMail(Request<Mail> request) {
        try {
            Mail mail = request.getPayload();
            saveMessage(mail);
            serverController.addLog("Messaggio salvato: " + mail);
        } catch (IOException e) {
            serverController.addLog("Errore durante il salvataggio del messaggio: " + e.getMessage());
        }
    }

    private Request<?> readDataFromClient() throws IOException, ClassNotFoundException {
        return (Request<?>) input.readObject();
    }

    private void sendResponse(Request<?> response) throws IOException {
        output.writeObject(response);
        output.flush();
    }

    private void saveMessage(Mail message) throws IOException {
        File file = new File(Structures.FILE_PATH);
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Impossibile creare il file: " + Structures.FILE_PATH);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.newLine();
            writer.write(message.toString());
        }
    }

    private void handleLoginCheck(Request<String> request) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = checkUserExists(userEmail);
        int responseCode;
        if (userExists) {
            serverController.addLog("User " + userEmail + " found. Sending LOGIN_OK.");
            responseCode = Structures.LOGIN_OK;
        } else {
            serverController.addLog("User " + userEmail + " not found. Sending LOGIN_ERROR.");
            responseCode = Structures.LOGIN_ERROR;
        }

        Request<String> response = new Request<>(responseCode, userEmail);
        sendResponse(response);
    }

    private void handleDestCheck(Request<String> request) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = checkUserExists(userEmail);
        int responseCode;
        if (userExists) {
            serverController.addLog("User " + userEmail + " found. Sending DEST_OK.");
            responseCode = Structures.DEST_OK;
        } else {
            serverController.addLog("User " + userEmail + " not found. Sending DEST_ERROR.");
            responseCode = Structures.DEST_ERROR;
        }

        Request<String> response = new Request<>(responseCode, userEmail);
        sendResponse(response);
    }

    private boolean checkUserExists(String email) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.USER_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }

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