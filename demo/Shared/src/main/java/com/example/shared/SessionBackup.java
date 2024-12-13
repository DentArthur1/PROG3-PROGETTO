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

    public SessionBackup(String userEmailBackup) {
        this.userEmailBackup = userEmailBackup;
    }

    public void startSession(ObservableList<Mail> emailBackup, int lastMailId) {
        // Method to start a new Inbox session with the given emails and last mail ID
        setEmailBackup(emailBackup);
        setLastMailId(lastMailId);
        sessionStarted = true;
    }

    public ObservableList<Mail> getEmailBackup() {
        return emailBackup;
    }

    public void setEmailBackup(ObservableList<Mail> emailBackup) {
        this.emailBackup = emailBackup;
    }

    public String getUserEmailBackup() {
        return userEmailBackup;
    }

    public boolean isSessionStarted() {
        return sessionStarted;
    }

    public int getLastMailId() {
        return lastMailId;
    }

    public void setLastMailId(int lastMailId) {
        this.lastMailId = lastMailId;
    }
}