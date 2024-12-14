package com.example.shared;

import java.io.Serializable;

public class Request<T> implements Serializable {
    /***
     * Classe per gestire i diversi tipi di richieste tra Client e Server
     * @param <T> : tipo generico per il payload della richiesta
     * il payload è il contenuto della richiesta che può essere di diversi tipi
     *           a seconda del tipo di richiesta che si vuole fare al server
     */

    private int REQUEST_CODE;
    private String AUTH_TOKEN;
    private T payload; // Dichiarazione di un riferimento generico
    private int lastMailId; // Aggiunto campo per tenere traccia dell'ultimo ID della mail

    /**
     * Costruttore per creare una nuova richiesta.
     * @param requestCode il codice della richiesta.
     * @param payload il payload della richiesta.
     * @param authToken il token di autenticazione.
     * @param lastMailId l'ultimo ID della mail.
     */

    public Request(int requestCode, T payload, String authToken, int lastMailId) {
        this.REQUEST_CODE = requestCode;
        this.payload = payload;
        this.AUTH_TOKEN = authToken;
        this.lastMailId = lastMailId;
    }

    // Getter e Setter
    /** Imposta il codice della richiesta. */

    public int getRequestCode() {
        return REQUEST_CODE;
    }

    /** Restituisce il token di autenticazione. */

    public String getAuthToken() {
        return AUTH_TOKEN;
    }

    public void setRequestCode(int requestCode) {
        this.REQUEST_CODE = requestCode;
    }

    /** Restituisce il payload della richiesta. */
    public T getPayload() {
        return payload;
    }

    /** Imposta il payload della richiesta. */
    public void setPayload(T payload) {
        this.payload = payload;
    }

    /** Restituisce l'ultimo ID della mail. */
    public int getLastMailId() {
        return lastMailId;
    }

    /** Imposta l'ultimo ID della mail. */
    public void setLastMailId(int lastMailId) {
        this.lastMailId = lastMailId;
    }

    @Override
    public String toString() {
        return "[REQUEST_CODE=" + REQUEST_CODE + ", payload=" + payload + ", lastMailId=" + lastMailId + "]";
    }
}