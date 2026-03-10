package com.sms.model;

public class AddExpenseResponse {
    private boolean data;
    private int responseCode;
    private String message;

    public AddExpenseResponse(boolean data, int responseCode, String message) {
        this.data = data;
        this.responseCode = responseCode;
        this.message = message;
    }

    public boolean isData() {
        return data;
    }

    public void setData(boolean data) {
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
}
