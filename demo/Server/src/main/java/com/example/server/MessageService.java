// MessageService.java
package com.example.server;

import com.example.shared.Mail;
import com.example.shared.Structures;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.util.*;

public class MessageService {
    private ServerManager servermanager;

    public MessageService(ServerManager servermanager) {
        this.servermanager = servermanager;
    }

    public synchronized ArrayList<String> loadMessages(String email) {
        ArrayList<String> messages = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(Structures.FILE_PATH + email.split("@")[0] + ".json"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                messages.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
        return messages;
    }

    public synchronized ArrayList<String> getMessagesByReceiver(String receiver, List<Integer> existingIds) {
        ArrayList<String> allMessages = loadMessages(receiver);
        ArrayList<String> filteredMessages = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String messageJson : allMessages) {
            try {
                JsonNode rootNode = mapper.readTree(messageJson);
                int id = rootNode.path("id").asInt();
                if (!existingIds.contains(id)) {
                    filteredMessages.add(messageJson);
                }
            } catch (IOException e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
                System.err.println("Problematic JSON: " + messageJson);
            }
        }
        return filteredMessages;
    }

    public synchronized void saveMessage(String receiver, String mailJson) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Structures.FILE_PATH + receiver.split("@")[0] + ".json", true))) {
            writer.write(mailJson);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }

    public synchronized void deleteMessages(String receiver, ArrayList<Integer> mailIds) {
        File file = new File(Structures.FILE_PATH + receiver.split("@")[0] + ".json");
        ArrayList<String> remainingMessages = new ArrayList<>();
        ObjectMapper mapper = new ObjectMapper();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                JsonNode rootNode = mapper.readTree(line);
                int id = rootNode.path("id").asInt();
                if (!mailIds.contains(id)) {
                    remainingMessages.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String message : remainingMessages) {
                writer.write(message);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }
}