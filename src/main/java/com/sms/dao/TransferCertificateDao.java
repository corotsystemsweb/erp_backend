package com.sms.dao;

import com.sms.model.TransferCertificateDetails;

import java.util.List;

public interface TransferCertificateDao {
    TransferCertificateDetails createTC(TransferCertificateDetails tc, String schoolCode);
    byte[] fetchTCPdf(Long tcId, String schoolCode);
    TransferCertificateDetails getTC(Long tcId, String schoolCode);
    List<TransferCertificateDetails> getAllTc(int sessionId, String schoolCode);
    TransferCertificateDetails updateTC(TransferCertificateDetails tc, Long tcId, String schoolCode);
    TransferCertificateDetails getTcByAdmissionNo(String admissionNo, String schoolCode);
}

