package com.sms.service;

import com.sms.model.PaymentDetails;

import java.util.List;

public interface PaymentModeService {
    public PaymentDetails addPaymentMode(PaymentDetails paymentDetails, String schoolCode) throws Exception;
    public PaymentDetails getPaymentModeById(int pmId, String schoolCode) throws Exception;
    public List<PaymentDetails> getAllPaymentMode(String schoolCode) throws Exception;
}
