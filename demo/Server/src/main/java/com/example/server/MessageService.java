package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import java.io.*;
import java.net.Socket;
import java.util.*;

public class MessageService {
    /**
     * Metodo per caricare i messaggi dal file
     * @return lista di messaggi e la aggiunge alla memoria del server
     */
    private ServerManager servermanager;

    public MessageService(ServerManager servermanager) {
        this.servermanager = servermanager;
    }

    public ArrayList<Mail> loadMessages(String email) {
        System.out.println("Loading Messages from " + servermanager.getClientFilePointer(email));
        ArrayList<Mail> messages = new ArrayList<>();
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH))) {
            String line;
            int pointer = servermanager.getClientFilePointer(email); // Valore del file_pointer

            while ((line = reader.readLine()) != null) {
                if (lineNumber < pointer) { // Salta solo le righe con indice < pointer
                    lineNumber++;
                    continue;
                }
                messages.add(Mail.fromLine(line)); // Aggiungi il messaggio dalla linea
                lineNumber++;
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
        }
        // Aggiorna il file_pointer del client che ha inviato l'email
        servermanager.updateClientFilePointer(email, lineNumber);
        return messages;
    }


    /**
     * Metodo per ottenere le mail ricevute da un determinato utente, da mettere nella inbox dopo il login
     * @param receiver email dell'utente di cui si vuole caricare la inbox
     * @return lista di email ricevute dall'utente
     *
     */
    public ArrayList<Mail> getMessagesByReceiver(String receiver, String email) {
        ArrayList<Mail> allMessages = loadMessages(email);
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