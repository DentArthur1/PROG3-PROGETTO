package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.*;

public class MessageService {
    /**
     * Method to load messages from the file
     * @return list of messages and adds them to the server's memory
     */
    private ServerManager servermanager;

    public MessageService(ServerManager servermanager) {
        this.servermanager = servermanager;
    }

    /**
     * Carica i messaggi da un file JSON e li ritorna come JSONArray senza convertire in oggetti Mail
     */
    public synchronized JSONArray loadMessages(String email) {
        JSONArray messages = new JSONArray();
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH + email.split("@")[0] + ".json"))) {
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }

            // Parse del contenuto JSON
            JSONArray jsonArray = new JSONArray(jsonContent.toString());

            // Aggiungi ogni oggetto JSON al JSONArray finale
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject mailJson = jsonArray.getJSONObject(i);

                // Aggiungi il JSONObject direttamente al JSONArray finale
                messages.put(mailJson);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        return messages;
    }

    /**
     * Method to get the emails received by a specific user, to be placed in the inbox after login
     * @param receiver email of the user whose inbox is to be loaded
     * @return list of emails received by the user
     */
    public synchronized String getMessagesByReceiver(String receiver, JSONArray existingIds) {
        // Carica tutti i messaggi per il receiver
        JSONArray allMessages = loadMessages(receiver);
        JSONArray filteredMessages = new JSONArray();

        // Itera su tutte le email e filtra quelle che non hanno un id presente in existingIds
        for (int i = 0; i < allMessages.length(); i++) {
            JSONObject message = allMessages.getJSONObject(i);
            int messageId = message.getInt(Structures.MAIL_ID_KEY);

            // Se l'ID non è presente in existingIds, aggiungi il messaggio al JSONArray filtrato
            boolean isIdPresent = false;
            for (int j = 0; j < existingIds.length(); j++) {
                if (existingIds.getInt(j) == messageId) {
                    isIdPresent = true;
                    break;
                }
            }
            if (!isIdPresent) {
                filteredMessages.put(message); // Aggiungi il messaggio alla lista filtrata
            }
        }
        // Restituisci la stringa del JSONArray filtrato
        return filteredMessages.toString();
    }

}