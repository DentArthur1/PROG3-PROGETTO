package com.example.client.modules;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Mail {

    private final StringProperty recipient;
    private final StringProperty subject;
    private final BooleanProperty selected;  // Proprietà per la selezione della checkbox

    public Mail(String recipient, String subject) {
        this.recipient = new SimpleStringProperty(recipient);
        this.subject = new SimpleStringProperty(subject);
        this.selected = new SimpleBooleanProperty(false);  // Impostato a false di default
    }

    public String getRecipient() {
        return recipient.get();
    }

    public StringProperty recipientProperty() {
        return recipient;
    }

    public String getSubject() {
        return subject.get();
    }

    public StringProperty subjectProperty() {
        return subject;
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
}
