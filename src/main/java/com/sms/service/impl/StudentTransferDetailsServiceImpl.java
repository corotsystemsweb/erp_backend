package com.sms.service.impl;

import com.sms.dao.StudentTransferCertificateDao;
import com.sms.model.SchoolDetails;
import com.sms.model.StudentTransferCertificateDetails;
import com.sms.service.SchoolService;
import com.sms.service.StudentTransferDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentTransferDetailsServiceImpl implements StudentTransferDetailsService {
    private final StudentTransferCertificateDao studentTransferCertificateDao;
    private final SchoolService schoolService;

    public StudentTransferDetailsServiceImpl(StudentTransferCertificateDao studentTransferCertificateDao, SchoolService schoolService) {
        this.studentTransferCertificateDao = studentTransferCertificateDao;
        this.schoolService = schoolService;
    }


    @Override
    public StudentTransferCertificateDetails issueTransferCertificate(StudentTransferCertificateDetails tc, String schoolCode) throws Exception {
        return studentTransferCertificateDao.createTC(tc, schoolCode);
    }

    @Override
    public byte[] generateTCAsPDF(Long tcId, String schoolCode) throws Exception {
        return studentTransferCertificateDao.fetchTCPdf(tcId, schoolCode);
    }

    @Override
    public StudentTransferCertificateDetails getTC(Long tcId, String schoolCode) {
        StudentTransferCertificateDetails tc =  studentTransferCertificateDao.getTC(tcId,schoolCode);
        if(tc == null){
            return null;
        }
        try {
            List<SchoolDetails> schoolList = schoolService.getAllSchoolDetails(schoolCode);

            if (schoolList != null && !schoolList.isEmpty()) {
                tc.setSchoolDetails(schoolList.get(0));
            }
        } catch (Exception e) {
            // skip if school data not available
        }
        try {
            SchoolDetails imageData = schoolService.getImage(schoolCode, 1);

            if (imageData != null && imageData.getSchoolImageString() != null) {
                tc.setSchoolImageBase64(imageData.getSchoolImageString());
            }
        } catch (Exception e) {
            // SILENT SKIP (image is optional)
        }
        return tc;
    }

    @Override
    public List<StudentTransferCertificateDetails> getAllTc(int sessionId, String schoolCode) {
        return studentTransferCertificateDao.getAllTc(sessionId,schoolCode);
    }

    @Override
    public StudentTransferCertificateDetails updateTC(StudentTransferCertificateDetails tc, Long tcId,String schoolCode) {
        return studentTransferCertificateDao.updateTC(tc,tcId,schoolCode);
    }
}
