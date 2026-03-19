package com.sms.model;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
public class PenaltyDetails {
    @Setter
    private int penaltyId;
    @Setter
    private int penaltyAmount;
    @Setter
    private String penaltyType;
    @Setter
    private  String description;
    private Timestamp systemDateTime;

    public Timestamp setSystemDateTime(Timestamp systemDateTime) {
        this.systemDateTime = systemDateTime;
        return systemDateTime;
    }
}


