
package com.sms.service.impl;

import com.sms.dao.TransferCertificateDao;
import com.sms.model.TransferCertificateDetails;
import com.sms.service.TransferCertificateService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransferCertificateServiceImpl implements TransferCertificateService {
    private final TransferCertificateDao transferCertificateDao;

    public TransferCertificateServiceImpl(TransferCertificateDao transferCertificateDao) {
        this.transferCertificateDao = transferCertificateDao;
    }

    @Override
    public TransferCertificateDetails issueTransferCertificate(TransferCertificateDetails tc, String schoolCode) throws Exception {
        return transferCertificateDao.createTC(tc, schoolCode);
    }

    @Override
    public byte[] generateTCAsPDF(Long tcId, String schoolCode) throws Exception {
        return transferCertificateDao.fetchTCPdf(tcId, schoolCode);
    }

    @Override
    public TransferCertificateDetails getTC(Long tcId, String schoolCode) {
        return transferCertificateDao.getTC(tcId,schoolCode);
    }

    @Override
    public List<TransferCertificateDetails> getAllTc(int sessionId, String schoolCode) {
        return transferCertificateDao.getAllTc(sessionId,schoolCode);
    }

    @Override
    public TransferCertificateDetails updateTC(TransferCertificateDetails tc, Long tcId,String schoolCode) {
        return transferCertificateDao.updateTC(tc,tcId,schoolCode);
    }

    @Override
    public TransferCertificateDetails getTcByAdmissionNo(String admissionNo, String schoolCode) {
        return transferCertificateDao.getTcByAdmissionNo(admissionNo, schoolCode);
    }
}

