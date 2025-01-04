package com.example.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

public class Mail implements Serializable {
    /**
     * Classe dati unificata per le Mail e i Messaggi.
     * Contiene i metodi per la conversione in stringa e viceversa, gestione della selezione,
     * e i campi condivisi tra Mail e Message.
     */
    private String sender;
    private String title;
    private String content;
    private String[] receivers;
    private LocalDateTime date;
    private transient BooleanProperty selected;
    private int id; // Aggiunto campo ID per tenere traccia dell'ultimo ID della mail

    /**
     * Costruttore per la creazione di una Mail
     *
     * @param sender    : mittente della mail
     * @param title     : oggetto della mail
     * @param content   : contenuto della mail
     * @param receivers : array dei destinatari
     * @param date      : data di invio della mail
     * @param id        : ID della mail
     */
    public Mail(String sender, String title, String content, String[] receivers, LocalDateTime date, int id) {
        this.sender = sender;
        this.title = title;
        this.content = content;
        this.receivers = receivers;
        this.date = date;
        this.selected = new SimpleBooleanProperty(false);
        this.id = id;
    }

    /**
     * Metodo di conversione da una riga di testo in un oggetto Mail.
     *
     * @param line : la riga da convertire
     * @return: oggetto Mail creato dalla riga
     */
    public static Mail fromLine(String line) {
        String[] parts = line.split("§");
        String[] receivs = parts[3].split(",");
        Mail mail = new Mail(parts[0], parts[1], parts[2], receivs, LocalDateTime.parse(parts[4]), Integer.parseInt(parts[5]));
        return mail;
    }

    // Getter e setter per tutti i campi

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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void recover_from_serialization() { // Recupera la proprietà `selected` dopo la deserializzazione
        this.selected = new SimpleBooleanProperty(false);
    }

    /**
     * Metodo `hashCode`.
     * Calcola l'hash code dell'oggetto `Mail` basandosi sui campi
     * `sender`, `title`, `content`, `receivers`, `date` e `id`
     * @return Hash code dell'oggetto `Mail`
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(sender, title, content, date, id);
        result = 31 * result + Arrays.hashCode(receivers);
        return result;
    }

    /**
     * Metodo `toString`.
     * Converte l'oggetto `Mail` in una stringa con un formato specifico.
     *
     * @return Rappresentazione testuale della mail.
     */
    /*@Override
    public String toString() {
        return sender + "§" + title + "§" + content + "§" + String.join(",", receivers) + "§" + date + "§" + id;
    }*/

    public static Mail fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Mail.class);
    }

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}