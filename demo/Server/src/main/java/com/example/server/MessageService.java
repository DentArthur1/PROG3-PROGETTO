package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
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
    public ArrayList<Mail> loadMessages(int file_pointer) {
        System.out.println("Loading Messages from " + file_pointer);
        ArrayList<Mail> messages = new ArrayList<>();
        // Carica il file da resources
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH))) {
            String line;
            int lineNumber = 0; // Contatore delle righe
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= file_pointer) {
                    continue; // Salta fino alla riga indicata da file_pointer
                }
                messages.add(Mail.fromLine(line)); // Aggiungi il messaggio dalla linea
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
    public ArrayList<Mail> getMessagesByReceiver(String receiver, int file_pointer) {
        ArrayList<Mail> allMessages = loadMessages(file_pointer);
        ArrayList<Mail> filteredMessages = new ArrayList<>();
        for (Mail message : allMessages) {
            for (String msg_receiver: message.getReceivers()){
                if (msg_receiver.equals(receiver)){
                    filteredMessages.add(message);
                }
            }
        }
        return filteredMessages;
    }

}