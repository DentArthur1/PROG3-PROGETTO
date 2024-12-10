package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import com.example.shared.Request;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Gestisce le connessioni multiple con i client.
 */
public class ClientManager {

    private final Socket clientSocket;
    private final MessageService messageService;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    public ClientManager(Socket socket) {
        this.clientSocket = socket;
        this.messageService = new MessageService();
    }

    public void handleClient() {
        try {
            // Inizializza gli stream all'inizio
            output = new ObjectOutputStream(clientSocket.getOutputStream());
            output.flush(); // Invia l'header
            input = new ObjectInputStream(clientSocket.getInputStream());

            // Ciclo per leggere le richieste dal client

                Request<?> request = readDataFromClient(); //unire le classi

                // Switch-case per gestire le richieste
                switch (request.getRequestCode()) {
                    case Structures.UPDATE_MAILS -> handleUpdateMails();
                    case Structures.PING -> handlePing();
                    case Structures.SEND_MAIL -> handleSendMail((Request<Mail>) request);
                    case Structures.LOGIN_CHECK -> handleLoginCheck((Request<String>) request);
                    case Structures.DEST_CHECK -> handleDestCheck((Request<String>) request);
                    default -> System.err.println("Codice richiesta non riconosciuto: " + request.getRequestCode());
                }

        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Errore nella gestione del client: ");
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }



    private void handleUpdateMails() throws IOException {
        ArrayList<Mail> messages = messageService.loadMessages();
        Request<ArrayList<Mail>> response = new Request<>(Structures.UPDATE_MAILS, messages);
        sendResponse(response);
        System.out.println("Email inviate con successo!");
    }

    private void handlePing() throws IOException {
        Request<String> response = new Request<>(Structures.PING, "pong");
        sendResponse(response);
        System.out.println("Ping gestito correttamente!");
    }

    private void handleSendMail(Request<Mail> request) {
        try {
            Mail mail = request.getPayload();
            saveMessage(mail);
            System.out.println("Messaggio salvato: " + mail);
        } catch (IOException e) {
            System.err.println("Errore durante il salvataggio del messaggio: " + e.getMessage());
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
            writer.write(message.toString());
            writer.newLine();
        }
    }
    private void handleLoginCheck(Request<String> request) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = checkUserExists(userEmail);
        int responseCode;
        if (userExists) {
            System.out.println("User " + userEmail + " found. Sending LOGIN_OK.");
            responseCode = Structures.LOGIN_OK;
        } else {
            System.out.println("User " + userEmail + " not found. Sending LOGIN_ERROR.");
            responseCode = Structures.LOGIN_ERROR;
        }

        Request<String> response = new Request<>(responseCode, userEmail);
        sendResponse(response);
    }

    private boolean checkUserExists(String email) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader("demo/Server/src/main/resources/users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(email)) {
                    return true;
                }
            }
        }
        return false;
    }
    private void handleDestCheck(Request<String> request) throws IOException {
        String userEmail = request.getPayload();
        boolean userExists = checkUserExists(userEmail);
        int responseCode;
        if (userExists) {
            System.out.println("User " + userEmail + " found. Sending DEST_OK.");
            responseCode = Structures.DEST_OK;
        } else {
            System.out.println("User " + userEmail + " not found. Sending DEST_ERROR.");
            responseCode = Structures.DEST_ERROR;
        }

        Request<String> response = new Request<>(responseCode, userEmail);
        sendResponse(response);
    }
    private void closeConnection() {
        try {
            if (input != null) input.close();
            if (output != null) output.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}
