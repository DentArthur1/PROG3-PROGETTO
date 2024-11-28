package com.example.client.modules;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.LocalDate;

public class Mail {
    //Classe dati per le Mail
    private String sender;
    private String title;
    private String[] receivers;
    private String content;
    private LocalDate date;
    private final BooleanProperty selected;  // Proprietà per la selezione della checkbox

    public Mail(String sender, String title, LocalDate dat, String[] receivs, String content) {
        this.sender = sender;
        this.title = title;
        this.date = dat;
        this.receivers = receivs;
        this.content = content;
        this.selected = new SimpleBooleanProperty(false);
    }

    public String getSender() {

        return sender;
    }

    public void setSender(String snd) {

        this.sender = snd;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String[] getReceivers() {
        return receivers;
    }
    public void setReceivers(String[] receivers) {
        this.receivers = receivers;
    }

    @Override
    public String toString() {
        return "id" + "§" + sender + "§" + title + "§" + content + "§" + String.join("", receivers) + "§" + date;
    }

    //Funzioni per la gestione della funzione "Selezione Mail"
    public boolean isSelected() {
        return selected.get();
    }

    public BooleanProperty selectedProperty() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected.set(selected);
    }

}
