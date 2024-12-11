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
    private int email_file_pointer = 0; //Per gestire l'invio delle mail nuove al client

    /**
     * DA CHIAMARE OGNI VOLTA CHE SI FA LOGOUT*/
    public void reset_file_pointer() {
        this.email_file_pointer = 0;
    }

    /**
     * DA CHIAMARE PRIMA O DOPO L'OPERAZIONE DI DELETE DELLE MAIL
     * AMOUNT = NUMERO DI MAIL ELIMINATE
     * NECESSARIO PER MANTENERE IL POINTER COERENTE*/
    public void decrease_file_pointer(int amount) {
        this.email_file_pointer -= amount;
    }
    /**
     *  Metodo per caricare i messaggi dal file in memoria
     * @return lista di messaggi
     */
    public ArrayList<Mail> loadMessages() {
        System.out.println("Loading Messages from " + this.email_file_pointer);
        ArrayList<Mail> messages = new ArrayList<>();
        // Carica il file da resources
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH))) {
            String line;
            int lineNumber = 0; // Contatore delle righe
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (lineNumber <= this.email_file_pointer) {
                    continue; // Salta fino alla riga indicata da file_pointer
                }
                messages.add(Mail.fromLine(line)); // Aggiungi il messaggio dalla linea
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file: " + e.getMessage());
        }
        //Aggiorno il pointer incrementale al file
        this.email_file_pointer += messages.size();
        return messages;
    }

    /**
     * Metodo per ottenere le mail ricevute da un determinato utente, da mettere nella inbox dopo il login
     * @param receiver email dell'utente di cui si vuole caricare la inbox
     * @return lista di email ricevute dall'utente
     *
     */
    public ArrayList<Mail> getMessagesByReceiver(String receiver) {
        ArrayList<Mail> allMessages = loadMessages();
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