package com.sms.dao;
import com.sms.model.FeeDetail;
import com.sms.model.TransportFeeDepositDetails;

import java.util.List;

public interface TransportFeeDepositDetailsDao {
    public List<TransportFeeDepositDetails> addTransportFeeDepositDetails(List<TransportFeeDepositDetails> transportFeeDepositDetails, String schoolCode) throws Exception;


    // NEW METHOD
    List<FeeDetail> findTransportFeeDetailsByTransactionId(
            String transactionId,
            int schoolId,
            String schoolCode
    ) throws Exception;


}
