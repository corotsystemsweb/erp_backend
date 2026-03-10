package com.sms.model;

public class DiscountCodeDetails {
    private int dcId;
    private String dcDescription;
    private int dcRate;
    private String dcRateType;
    private String additionalInfo;

    public int getDcId() {
        return dcId;
    }

    public void setDcId(int dcId) {
        this.dcId = dcId;
    }

    public String getDcDescription() {
        return dcDescription;
    }

    public void setDcDescription(String dcDescription) {
        this.dcDescription = dcDescription;
    }

    public int getDcRate() {
        return dcRate;
    }

    public void setDcRate(int dcRate) {
        this.dcRate = dcRate;
    }

    public String getDcRateType() {
        return dcRateType;
    }

    public void setDcRateType(String dcRateType) {
        this.dcRateType = dcRateType;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
