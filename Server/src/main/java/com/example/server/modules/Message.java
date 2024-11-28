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
    private String receiver;
    private LocalDate date;

    public Message(String id, String sender, String title, String content, String receiver) {
        this.id = id;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.receiver = receiver;
        this.date = LocalDate.now();
    }

    /**
     * TODO: da modificare per cambiare il separatore
     * metodo provvisorio per la conversione di una riga di testo in un oggetto Message
     * @param line
     * @return : oggetto Message
     */
    public static Message fromLine(String line) {
        String[] parts = line.split("§");
        return new Message(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return id + ";" + sender + ";" + title + ";" + content + ";" + receiver;
    }
}
