package com.example.server.modules;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class Message implements Serializable {
    private static final String SEPARATOR = "\\|\\|\\|###\\|\\|\\|"; // Separatore complesso
    private int id;
    private String sender;
    private String content;
    private List<String> recipients;

    public Message(int id, String sender, String content, String recipients) {
        this.id = id;
        this.sender = sender;
        this.content = content;
        this.recipients = Arrays.asList(recipients.split(","));
    }

    // Metodo statico per convertire una riga del file in un oggetto Message
    public static Message fromLine(String line) {
        String[] parts = line.split(SEPARATOR, -1); // Usa -1 per includere tutti i segmenti
        int id = Integer.parseInt(parts[0]);
        return new Message(id, parts[1], parts[2], parts[3]);
    }

    @Override
    public String toString() {
        return "ID: " + id +
                ", Mittente: " + sender +
                ", Messaggio: \"" + content + "\"" +
                ", Destinatari: " + recipients;
    }

    public int getId() {
        return id;
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public List<String> getRecipients() {
        return recipients;
    }
}
