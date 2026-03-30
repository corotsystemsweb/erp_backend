package com.sms.service;

import com.sms.model.StudentEnquiryFormDetails;
import org.springframework.web.multipart.MultipartFile;

public interface StudentEnquiryFormService {
    public StudentEnquiryFormDetails saveStudentEnquiry(StudentEnquiryFormDetails studentEnquiryFormDetails, MultipartFile file, String schoolCode) throws Exception;
}
