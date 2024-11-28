package com.example.server.modules;

import java.time.LocalDateTime;

/**
 * Classe per la gestione dei messaggi,
 * contiene i metodi per la conversione di un messaggio in una stringa e viceversa
 * @param id : id del messaggio
 * @param sender : mittente del messaggio
 * @param title : oggetto del messaggio
 * @param content : contenuto del messaggio
 * @param receivers : array di destinatari del messaggio
 * @param date : data di invio del messaggio con ora
 */
public class Message {
    private String id;
    private String sender;
    private String title;
    private String content;
    private String[] receivers;
    private LocalDateTime date;

    public Message(String id, String sender, String title, String content, String[] receivers, LocalDateTime date) {
        this.id = id;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.receivers = receivers;
        this.date = date;
    }

    /**
     *
     * Metodo per la conversione di una riga di testo in un oggetto Message
     * @param line
     * @return : oggetto Message
     */
    public static Message fromLine(String line) {
        String[] parts = line.split("§");
        String[] receivs = parts[4].split(",");
        return new Message(parts[0], parts[1], parts[2], parts[3], receivs, LocalDateTime.parse(parts[5]));
    }

    public String[] receivers() {
        return this.receivers;
    }

    @Override
    public String toString() {
        return id + "§" + sender + "§" + title + "§" + content + "§" + String.join("", receivers) + "§" + date;
    }
}
