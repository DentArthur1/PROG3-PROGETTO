package com.example.shared;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Mail implements Serializable{
    /**
     * Classe dati unificata per le Mail e i Messaggi.
     * Contiene i metodi per la conversione in stringa e viceversa, gestione della selezione,
     * e i campi condivisi tra Mail e Message.
     */
    private String id;
    private String sender;
    private String title;
    private String content;
    private String[] receivers;
    private LocalDateTime date;
    private transient BooleanProperty selected;

    /**
     * Costruttore per la creazione di una Mail
     * @param id : id del messaggio
     * @param sender : mittente della mail
     * @param title : oggetto della mail
     * @param content : contenuto della mail
     * @param receivers : array dei destinatari
     * @param date : data di invio della mail
     */
    public Mail(String id, String sender, String title, String content, String[] receivers, LocalDateTime date) {
        this.id = id;
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.receivers = receivers;
        this.date = date;
        this.selected = new SimpleBooleanProperty(false);
    }

    /**
     * Metodo di conversione da una riga di testo in un oggetto Mail.
     * @param line : la riga da convertire
     * @return : oggetto Mail creato dalla riga
     */
    public static Mail fromLine(String line) {
        String[] parts = line.split("§");
        String[] receivs = parts[4].split(",");
        return new Mail(parts[0], parts[1], parts[2], parts[3], receivs, LocalDateTime.parse(parts[5]));
    }

    // Getter e setter per tutti i campi

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getReceivers() {
        return receivers;
    }

    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

    /**
     * Metodo `toString`.
     * Converte l'oggetto `Mail` in una stringa con un formato specifico.
     * @return Rappresentazione testuale della mail.
     */
    @Override
    public String toString() {
        return id + "§" + sender + "§" + title + "§" + content + "§" + String.join(",", receivers) + "§" + date;
    }
}
