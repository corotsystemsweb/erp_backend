package com.sms.dao;

import com.sms.model.PaymentDetails;
import com.sms.model.SessionDetails;

import java.util.List;


public interface PaymentModeDao {
    public PaymentDetails addPaymentMode(PaymentDetails paymentDetails, String schoolCode) throws Exception;
    public PaymentDetails getPaymentModeById(int pmId, String schoolCode) throws Exception;
    public List<PaymentDetails> getAllPaymentMode(String schoolCode) throws Exception;
}
