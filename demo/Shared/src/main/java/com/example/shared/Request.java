package com.example.shared;

import java.io.Serializable;
public class Request<T> implements Serializable {
    /***
     * Classe per gestire i diversi tipi di richieste tra Client e Server
     */

    private int REQUEST_CODE;
    private String AUTH_TOKEN;
    private T payload; // Dichiarazione di un riferimento generico

    // Costruttore
    public Request(int requestCode, T payload, String authToken) {
        this.REQUEST_CODE = requestCode;
        this.payload = payload;
        this.AUTH_TOKEN = authToken;
    }

    // Getter e Setter
    public int getRequestCode() {
        return REQUEST_CODE;
    }

    public String getAuthToken() {
        return AUTH_TOKEN;
    }

    public void setRequestCode(int requestCode) {
        this.REQUEST_CODE = requestCode;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "[REQUEST_CODE=" + REQUEST_CODE + ", payload=" + payload + "]";
    }
}
