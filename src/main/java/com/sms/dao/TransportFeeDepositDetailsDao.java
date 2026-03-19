package com.sms.dao;

import com.sms.model.TransportFeeDepositDetails;

import java.util.List;

public interface TransportFeeDepositDetailsDao {
    public List<TransportFeeDepositDetails> addTransportFeeDepositDetails(List<TransportFeeDepositDetails> transportFeeDepositDetails, String schoolCode) throws Exception;

}
