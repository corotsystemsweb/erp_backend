package com.sms.dao;

import com.sms.model.TransportFeeDepositDetails;

public interface TransportFeeDepositDao {
    public TransportFeeDepositDetails addTransportFeeDeposit(TransportFeeDepositDetails transportFeeDepositDetails, String schoolCode) throws Exception;
}
