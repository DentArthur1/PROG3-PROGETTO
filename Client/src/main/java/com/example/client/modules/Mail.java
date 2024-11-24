package com.example.client.modules;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;

public class Mail {
    //Classe dati per le Mail
    private String sender;
    private String subject;
    private String[] receivers;
    private LocalDate date;
    private final BooleanProperty selected;  // Proprietà per la selezione della checkbox

    public Mail(String recipient, String subject, LocalDate dat, String[] receivs) {
        this.sender = recipient;
        this.subject = subject;
        this.date = dat;
        this.receivers = receivs;
        this.selected = new SimpleBooleanProperty(false);
    }

    public String getSender() {

        return sender;
    }

    public void setSender(String snd) {

        this.sender = snd;
    }

    public String getSubject() {

        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;

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
