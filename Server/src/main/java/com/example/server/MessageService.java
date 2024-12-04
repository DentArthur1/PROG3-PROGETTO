package com.example.server;

import com.example.server.modules.Message;
import com.example.server.modules.Structures;
import java.io.*;
import java.util.*;

public class MessageService {
    /**
     * Metodo per caricare i messaggi dal file
     * @return lista di messaggi e la aggiunge alla memoria del server
     */

    public void MessageService(){

    }
    /**
     *  Metodo per caricare i messaggi dal file in memoria
     * @return lista di messaggi
     */
    public ArrayList<Message> loadMessages() {
        ArrayList<Message> messages = new ArrayList<>();
        File file = new File(Structures.FILE_PATH);

        if (!file.exists()) {
            System.out.println("Il file non esiste: " + Structures.FILE_PATH);
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
    /**
     * Metodo per ottenere le mail ricevute da un determinato utente, da mettere nella inbox dopo il login
     * @param receiver email dell'utente di cui si vuole caricare la inbox
     * @return lista di email ricevute dall'utente
     *
     */
    public List<Message> getMessagesByReceiver(String receiver) {
        ArrayList<Message> allMessages = loadMessages();
        ArrayList<Message> filteredMessages = new ArrayList<>();
        for (Message message : allMessages) {
            for (String msg_receiver: message.receivers()){
                if (msg_receiver.equals(receiver)){
                    filteredMessages.add(message);
                }
            }
        }
        return filteredMessages;
    }

}