package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
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

    public synchronized ArrayList<Mail> loadMessages(String email) {
        ArrayList<Mail> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH + email.split("@")[0] + ".txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(Mail.fromLine(line));
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
    public synchronized ArrayList<Mail> getMessagesByReceiver(String receiver, List<Integer> existingIds) {
        ArrayList<Mail> allMessages = loadMessages(receiver);
        ArrayList<Mail> filteredMessages = new ArrayList<>();
        //Prendo solo le mail che il client non ha ancora
        for (Mail message : allMessages) {
            if (!existingIds.contains(message.getId())) {
                filteredMessages.add(message);
            }
        }
        return filteredMessages;
    }


}