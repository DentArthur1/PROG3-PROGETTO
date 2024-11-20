package com.example.client.modules;


import javafx.collections.ObservableList;

public class SessionBackup {


    private ObservableList<Mail> emailBackup;
    private String userEmailBackup;
    public boolean sessionStarted;


    public SessionBackup(String userEmailBackup) {
        this.userEmailBackup = userEmailBackup;
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
    public void setUserEmailBackup(String email){
        this.userEmailBackup = email;
    }


    public boolean isSessionStarted() {
        return sessionStarted;
    }

}
