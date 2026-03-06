package com.sms.service;

import com.sms.model.TransferCertificateDetails;

import java.util.List;



public interface TransferCertificateService {
    TransferCertificateDetails issueTransferCertificate(TransferCertificateDetails tc, String schoolCode) throws Exception;
    byte[] generateTCAsPDF(Long tcId, String schoolCode) throws Exception;
    TransferCertificateDetails getTC(Long tcId, String schoolCode);
    List<TransferCertificateDetails> getAllTc(int sessionId, String schoolCode);
    TransferCertificateDetails updateTC(TransferCertificateDetails tc, Long tcId, String schoolCode);
    TransferCertificateDetails getTcByAdmissionNo(String admissionNo, String schoolCode);
}