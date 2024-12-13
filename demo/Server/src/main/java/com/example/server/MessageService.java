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
        System.out.println("Loading Messages from " + servermanager.getClientFilePointer(email));
        ArrayList<Mail> messages = new ArrayList<>();
        int lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH))) {
            String line;
            int pointer = servermanager.getClientFilePointer(email); // Value of the file pointer

            while ((line = reader.readLine()) != null) {
                if (lineNumber < pointer) { // Skip lines with index < pointer
                    lineNumber++;
                    continue;
                }
                messages.add(Mail.fromLine(line)); // Add the message from the line
                lineNumber++;
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
    public synchronized ArrayList<Mail> getMessagesByReceiver(String receiver, String email) {
        ArrayList<Mail> allMessages = loadMessages(email);
        ArrayList<Mail> filteredMessages = new ArrayList<>();
        for (Mail message : allMessages) {
            if (!message.isModified()) { // Filtra le mail modificate
                for (String msg_receiver : message.getReceivers()) {
                    if (msg_receiver.equals(receiver)) {
                        filteredMessages.add(message);
                    }
                }
            }
        }
        return filteredMessages;
    }

    public synchronized ArrayList<Mail> loadAllMailfromUser() {
        ArrayList<Mail> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                Mail mail = Mail.fromLine(line);
                messages.add(mail);

            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return messages;
    }
}