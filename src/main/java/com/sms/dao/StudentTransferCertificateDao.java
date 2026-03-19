package com.sms.dao;

import com.sms.model.StudentTransferCertificateDetails;

import java.util.List;

public interface StudentTransferCertificateDao {
    StudentTransferCertificateDetails createTC(StudentTransferCertificateDetails tc, String schoolCode);
    byte[] fetchTCPdf(Long tcId, String schoolCode);
    StudentTransferCertificateDetails getTC(Long tcId, String schoolCode);
    List<StudentTransferCertificateDetails> getAllTc(int sessionId, String schoolCode);
    StudentTransferCertificateDetails updateTC(StudentTransferCertificateDetails tc, Long tcId, String schoolCode);
}
