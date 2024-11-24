package com.example.client.modules;


import javafx.collections.ObservableList;

public class SessionBackup {
    //Classe per gestire il backup dei dati del client,
    //utilizzabile in caso di perdita di connessione o per un utilizzo del servizio offline

    private ObservableList<Mail> emailBackup;
    private String userEmailBackup;
    private boolean sessionStarted;

    public SessionBackup(String userEmailBackup) {
        this.userEmailBackup = userEmailBackup;
    }

    public void startSession(ObservableList<Mail> emailBackup) {
        //Metodo per iniziare una nuova sessione Inbox con le email date come parametro
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
