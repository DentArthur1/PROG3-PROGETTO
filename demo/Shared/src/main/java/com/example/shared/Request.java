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
    private T payload; // Dichiarazione di un riferimento generico
    private String requestId;
    /**
     * Costruttore per creare una nuova richiesta.
     * @param requestCode il codice della richiesta.
     * @param payload il payload della richiesta.
     * @param requestId la mail di chi ha fatto la richiesta.
     */

    public Request(int requestCode, T payload, String requestId) {
        this.REQUEST_CODE = requestCode;
        this.payload = payload;
        this.requestId = requestId;

    }

    // Getter e Setter
    /** Imposta il codice della richiesta. */

    public int getRequestCode() {
        return REQUEST_CODE;
    }

    /** Restituisce la mail di chi ha fatto la richiesta. */

    public String getRequestId() {
        return this.requestId;
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




    @Override
    public String toString() {
        return "[REQUEST_CODE=" + REQUEST_CODE + ", payload=" + payload + "]";
    }
}