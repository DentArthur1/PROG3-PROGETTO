package com.example.shared;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

public class Request<T> implements Serializable {
    private int REQUEST_CODE;
    private T payload;
    private String requestId;

    // Default constructor
    public Request() {
    }

    public Request(int requestCode, T payload, String requestId) {
        this.REQUEST_CODE = requestCode;
        this.payload = payload;
        this.requestId = requestId;
    }

    public int getRequestCode() {
        return REQUEST_CODE;
    }

    public String getRequestId() {
        return this.requestId;
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

    public String toJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> Request<T> fromJson(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Request.class);
    }

    @Override
    public String toString() {
        return "[REQUEST_CODE=" + REQUEST_CODE + ", payload=" + payload + "]";
    }
}