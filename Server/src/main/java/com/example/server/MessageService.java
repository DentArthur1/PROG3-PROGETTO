package com.example.server;

import com.example.server.modules.Message;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MessageService {
    private static final String FILE_PATH = "src/main/resources/data.txt";
    private ServerController server;
    /**
     * Metodo per caricare i messaggi dal file
     * @return lista di messaggi e la aggiunge alla memoria del server
     */

    public void MessageService(){
        this.server = new ServerController();
    }

    public List<Message> loadMessages() {
        List<Message> messages = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            System.out.println("Il file non esiste: " + FILE_PATH);
            return messages;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(Message.fromLine(line));
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
        }
        return messages;
    }
    public void saveMessage(Message message) throws IOException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            throw new IOException("File does not exists "+ FILE_PATH);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(message.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file: " + e.getMessage());
        }
    }
    /**
     * Metodo per ottenere le mail ricevute da un determinato utente, da mettere nella inbox dopo il login
     * @param receiver email dell'utente di cui si vuole caricare la inbox
     * @return lista di email ricevute dall'utente
     *
     */
    public List<Message> getMessagesByReceiver(String receiver) {
        List<Message> allMessages = loadMessages();
        List<Message> filteredMessages = new ArrayList<>();
        for (Message message : allMessages) {
            if (message.receivers().equals(receiver)) {
                filteredMessages.add(message);
            }
        }
        return filteredMessages;
    }

    public void saveMessageFromClient(Socket clientSocket) throws IOException{
       String message = server.getDataFromClient(clientSocket);
       Message parsed_message = Message.fromLine(message);
       saveMessage(parsed_message);
    }
}
