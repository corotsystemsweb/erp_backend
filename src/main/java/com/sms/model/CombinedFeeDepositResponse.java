package com.sms.model;

import lombok.Data;

import java.util.List;

@Data
public class CombinedFeeDepositResponse {
    private List<FeeDepositDetails> academicFeeDetails;
    private List<TransportFeeDepositDetails> transportFeeDetails;
}
