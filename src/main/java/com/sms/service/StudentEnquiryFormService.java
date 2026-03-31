package com.sms.service;

import com.sms.model.StudentEnquiryFormDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentEnquiryFormService {
    public StudentEnquiryFormDetails saveStudentEnquiry(StudentEnquiryFormDetails studentEnquiryFormDetails, MultipartFile file, String schoolCode) throws Exception;
    public List<StudentEnquiryFormDetails> getAllStudentEnquiry(String schoolCode) throws Exception;
}
