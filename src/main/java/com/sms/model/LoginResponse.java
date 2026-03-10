package com.sms.model;

public class LoginResponse {
    private String data;
    private int responseCode;
    private String message;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public LoginResponse(String data, int responseCode, String message) {
        this.data = data;
        this.responseCode = responseCode;
        this.message = message;
    }
}
