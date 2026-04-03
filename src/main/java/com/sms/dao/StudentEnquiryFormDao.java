package com.sms.dao;

import com.sms.model.StudentDetails;
import com.sms.model.StudentEnquiryFormDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentEnquiryFormDao {
    public boolean addImage(MultipartFile file, String schoolCode, int studentId) throws Exception;
    public StudentEnquiryFormDetails getImage(String schoolCode, int studentId) throws Exception;
    public StudentEnquiryFormDetails saveStudentEnquiry(StudentEnquiryFormDetails studentEnquiryFormDetails, String schoolCode) throws Exception;
    public List<StudentEnquiryFormDetails> getAllStudentEnquiry(String status, String schoolCode) throws Exception;
    public StudentEnquiryFormDetails getStudentEnquiryById(int studentEnquiryId, String schoolCode) throws Exception;
    public StudentEnquiryFormDetails updateStudentEnquiryById(StudentEnquiryFormDetails studentEnquiryFormDetails, String schoolCode) throws Exception;
    public boolean deleteStudentEnquiry(int studentEnquiryId,String schoolCode) throws Exception;
}
