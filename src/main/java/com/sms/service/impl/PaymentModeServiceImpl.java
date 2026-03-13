package com.sms.service.impl;

import com.sms.dao.PaymentModeDao;
import com.sms.model.PaymentDetails;
import com.sms.service.PaymentModeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentModeServiceImpl implements PaymentModeService {

    @Autowired
    private PaymentModeDao paymentModeDao;
    @Override
    public PaymentDetails addPaymentMode(PaymentDetails paymentDetails, String schoolCode) throws Exception {
        return paymentModeDao.addPaymentMode(paymentDetails, schoolCode);
    }
    @Override
    public PaymentDetails getPaymentModeById(int pmId, String schoolCode) throws Exception {
        return paymentModeDao.getPaymentModeById(pmId, schoolCode);
    }

    @Override
    public List<PaymentDetails> getAllPaymentMode(String schoolCode) throws Exception {
        return paymentModeDao.getAllPaymentMode(schoolCode);
    }
}
