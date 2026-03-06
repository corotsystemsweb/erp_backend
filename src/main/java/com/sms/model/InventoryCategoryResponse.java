package com.sms.model;

public class InventoryCategoryResponse {
    private boolean data;
    private int responseCode;
    private String message;

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

    public InventoryCategoryResponse(boolean data, int responseCode, String message) {
        this.data = data;
        this.responseCode = responseCode;
        this.message = message;
    }
}
