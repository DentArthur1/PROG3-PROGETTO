package com.example.shared;


import javafx.collections.ObservableList;

public class SessionBackup {

    /**
     * Questa classe è progettata per gestire il backup della sessione del client.
     * È utile in caso di perdita di connessione o per un utilizzo del servizio offline
     */

    private ObservableList<Mail> emailBackup;
    private String userEmailBackup;
    private boolean sessionStarted; /** flag per indicare se una sessione è attiva */

    public SessionBackup(String userEmailBackup) {
        this.userEmailBackup = userEmailBackup;
    }

    public void startSession(ObservableList<Mail> emailBackup) {
        /** Metodo per iniziare una nuova sessione Inbox con le email date come parametro */
        setEmailBackup(emailBackup);
        sessionStarted = true;
    }

    public ObservableList<Mail> getEmailBackup() {
        return emailBackup;
    }
    private void setEmailBackup(ObservableList<Mail> emailBackup) {
        this.emailBackup = emailBackup;
    }

    public String getUserEmailBackup() {
        return userEmailBackup;
    }

    public boolean isSessionStarted() {
        return sessionStarted;
    }

}
