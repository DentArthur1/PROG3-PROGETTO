package com.example.server;

import com.example.server.modules.Message;
import com.example.server.modules.Structures;
import javafx.application.Platform;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Gestisce le connessioni multiple con i client
 */
public class ClientManager {

    private Socket clientSocket;
    private MessageService messageService;

    public ClientManager(Socket clientSocket) {
        this.clientSocket = clientSocket;
        this.messageService = new MessageService();
    }

    public void handleClient() {
        new Thread(() -> {
            try {
                String data = readDataFromClient();
                if (data.equals("Gimme")){ //Il client richiede le mail
                    ArrayList<Message> messages = messageService.loadMessages();
                    //Serializzo l'array di messaggi
                    String serialized_messages = "";
                    for (Message message : messages) {
                        if (serialized_messages.equals("")){
                            serialized_messages = message.toString();
                        } else {
                            serialized_messages = serialized_messages + "§§§" + message.toString();
                        }
                    }
                    //Scrivo il messaggio serializzato sul socket
                    sendEmails(serialized_messages);
                } else {
                    Message parsedMessage = Message.fromLine(data);
                    saveMessage(parsedMessage);
                    Platform.runLater(() -> {
                        System.out.println("Messaggio salvato: " + parsedMessage);
                    });
                }
            } catch (IOException e) {
                System.err.println("Errore nella gestione del client: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Errore nella chiusura del socket del client: " + e.getMessage());
                }
            }
        }).start();
    }

    /**
     * Legge i dati inviati dal client.
     */
    private String readDataFromClient() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        return in.readLine();
    }

    /**
     * Salva un messaggio nel file.
     */
    private void saveMessage(Message message) throws IOException {
        File file = new File(Structures.FILE_PATH);
        if (!file.exists()) {
            throw new IOException("File inesistente: " + Structures.FILE_PATH);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(message.toString());
            writer.newLine();
        }
    }

    private void sendEmails(String serialized) {
        //Invia le email al client
        try (PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {
            System.out.println("Email inviate con successo dal server!\n");
            out.println(serialized);
            //System.out.println(serialized);
        } catch (IOException e) {
            System.err.println("Errore nell'invio delle mail dal server: " + e.getMessage());
        }

    }

}