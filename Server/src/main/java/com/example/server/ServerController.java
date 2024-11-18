package com.example.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.example.server.modules.Message;


public class ServerController {
    private static final String FILE_PATH = "src/main/resources/data.txt"; // Percorso del file dei messaggi

    public static void main(String[] args) {
        List<Message> messages = loadMessages();
        if (messages.isEmpty()) {
            System.out.println("Nessun messaggio trovato.");
        } else {
            System.out.println("Messaggi letti dal file:");
            for (Message message : messages) {
                System.out.println(message);
            }
        }
    }

    // Metodo per leggere i messaggi dal file
    private static List<Message> loadMessages() {
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
}
