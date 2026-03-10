package com.sms.service;

import com.sms.model.TransportFeeDepositDetails;
import com.sms.model.TransportFeeDepositRequest;

import java.util.List;

public interface TransportFeeDepositDetailsService {
    public List<TransportFeeDepositDetails> addTransportDepositDetails(TransportFeeDepositRequest transportFeeDepositRequest, String schoolCode) throws Exception;
}
