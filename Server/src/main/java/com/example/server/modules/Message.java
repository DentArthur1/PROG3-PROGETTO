package com.example.server.modules;

import java.time.LocalDate;

/**
 * Classe per la gestione dei messaggi,
 * TODO: da modificare per renderla conforme alla versione client
 */
public class Message {
    private String id;
    private String sender;
    private String title;
    private String content;
    private String[] receivers;
    private LocalDate date;

    public Message(String id, String sender, String title, String content, String[] receivers, LocalDate date) {
        this.id = id;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.receivers = receivers;
        this.date = date;
    }

    /**
     * TODO: da modificare per cambiare il separatore
     * metodo provvisorio per la conversione di una riga di testo in un oggetto Message
     * @param line
     * @return : oggetto Message
     */
    public static Message fromLine(String line) {
        String[] parts = line.split("§");
        String[] receivs = parts[4].split(",");
        return new Message(parts[0], parts[1], parts[2], parts[3], receivs, LocalDate.parse(parts[5]));
    }

    public String[] receivers() {
        return this.receivers;
    }

    @Override
    public String toString() {
        return id + "§" + sender + "$" + title + "$" + content + "$" + String.join("", receivers) + "$" + date;
    }
}
