package com.sms.service.impl;

import com.sms.dao.TransportFeeDepositDao;
import com.sms.dao.TransportFeeDepositDetailsDao;
import com.sms.model.TransportFeeDepositDetails;
import com.sms.model.TransportFeeDepositRequest;
import com.sms.service.TransportFeeDepositDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransportFeeDepositDetailsServiceImpl implements TransportFeeDepositDetailsService {

    @Autowired
    private TransportFeeDepositDao transportFeeDepositDao;

    @Autowired
    private TransportFeeDepositDetailsDao transportFeeDepositDetailsDao;

    @Override
    public List<TransportFeeDepositDetails> addTransportDepositDetails(TransportFeeDepositRequest transportFeeDepositRequest, String schoolCode) throws Exception {
        if (transportFeeDepositRequest == null || transportFeeDepositRequest.getTransportFeeDepositDetails() == null || transportFeeDepositRequest.getTransportFeeDepositDetails().isEmpty()) {
            throw new IllegalArgumentException("Transport fee deposit details cannot be empty");
        }
        //calculate total amount
        double totalAmount = transportFeeDepositRequest.getTransportFeeDepositDetails().stream().mapToDouble(TransportFeeDepositDetails::getAmountPaid).sum();

        TransportFeeDepositDetails feeDepositDetails = new TransportFeeDepositDetails();

        feeDepositDetails.setSchoolId(transportFeeDepositRequest.getSchoolId());
        feeDepositDetails.setSessionId(transportFeeDepositRequest.getSessionId());
        feeDepositDetails.setClassId(transportFeeDepositRequest.getClassId());
        feeDepositDetails.setSectionId(transportFeeDepositRequest.getSectionId());
        feeDepositDetails.setStudentId(transportFeeDepositRequest.getStudentId());
        feeDepositDetails.setRouteId(transportFeeDepositRequest.getRouteId());
        feeDepositDetails.setPaymentMode(transportFeeDepositRequest.getPaymentMode());
        feeDepositDetails.setTotalAmountPaid(totalAmount);
        feeDepositDetails.setPaymentReceivedBy(transportFeeDepositRequest.getPaymentReceivedBy());
        feeDepositDetails.setPaymentDate(transportFeeDepositRequest.getPaymentDate() != null ? transportFeeDepositRequest.getPaymentDate() : LocalDateTime.now());
        feeDepositDetails.setTransactionId(transportFeeDepositRequest.getTransactionId());
        feeDepositDetails.setPaymentDescription(transportFeeDepositRequest.getPaymentDescription());
        feeDepositDetails.setStatus(transportFeeDepositRequest.getStatus());

        TransportFeeDepositDetails transportFeeDepositDetails = transportFeeDepositDao.addTransportFeeDeposit(feeDepositDetails, schoolCode);

        //Set generated tfd_id into each detail
        for (TransportFeeDepositDetails detail : transportFeeDepositRequest.getTransportFeeDepositDetails()) {
            detail.setTfdId(transportFeeDepositDetails.getTfdId());
        }

        //Insert into Transport Fee Deposit Details
        List<TransportFeeDepositDetails> addDetails = transportFeeDepositDetailsDao.addTransportFeeDepositDetails(transportFeeDepositRequest.getTransportFeeDepositDetails(), schoolCode);

        return addDetails;
    }
}
