package com.example.client.modules;

public class Mail {
    private String recipient;
    private String subject;

    public Mail(String recipient, String subject) {
        this.recipient = recipient;
        this.subject = subject;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
