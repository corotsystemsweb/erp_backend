package com.sms.service;

import com.sms.model.StudentTransferCertificateDetails;

import java.util.List;

public interface StudentTransferDetailsService {
    StudentTransferCertificateDetails issueTransferCertificate(StudentTransferCertificateDetails tc, String schoolCode) throws Exception;
    byte[] generateTCAsPDF(Long tcId, String schoolCode) throws Exception;
    StudentTransferCertificateDetails getTC(Long tcId, String schoolCode);
    List<StudentTransferCertificateDetails> getAllTc(int sessionId, String schoolCode);
    StudentTransferCertificateDetails updateTC(StudentTransferCertificateDetails tc, Long tcId, String schoolCode);
}
