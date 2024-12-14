package com.example.shared;

import javafx.collections.ObservableList;

public class SessionBackup {

    /**
     * This class is designed to manage the client's session backup.
     * It is useful in case of connection loss or for offline service usage.
     */

    private ObservableList<Mail> emailBackup;
    private String userEmailBackup;
    private boolean sessionStarted; // Flag to indicate if a session is active
    private int lastMailId; // Added field to track the last mail ID

    /** Costruttore per creare una nuova sessione di backup. */

    public SessionBackup(String userEmailBackup) {
        this.userEmailBackup = userEmailBackup;
    }

    /**
     * Metodo per avviare una nuova sessione di backup.
     * @param emailBackup : backup delle email
     * @param lastMailId : ID dell'ultima mail
     */

    public void startSession(ObservableList<Mail> emailBackup, int lastMailId) {
        // Method to start a new Inbox session with the given emails and last mail ID
        setEmailBackup(emailBackup);
        setLastMailId(lastMailId);
        sessionStarted = true;
    }

    /** Metodo per terminare la sessione di backup. */
    public ObservableList<Mail> getEmailBackup() {
        return emailBackup;
    }

    /** Restituisce il backup delle email. */
    public void setEmailBackup(ObservableList<Mail> emailBackup) {
        this.emailBackup = emailBackup;
    }

    /** Restituisce l'indirizzo email dell'utente. */
    public String getUserEmailBackup() {
        return userEmailBackup;
    }

    /** Imposta l'indirizzo email dell'utente. */
    public boolean isSessionStarted() {
        return sessionStarted;
    }

    /** Restituisce true se la sessione è attiva. */
    public int getLastMailId() {
        return lastMailId;
    }

    /** Restituisce l'ultimo ID della mail. */
    public void setLastMailId(int lastMailId) {
        this.lastMailId = lastMailId;
    }
}