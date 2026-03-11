package com.sms.service;

import com.sms.model.SubjectDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface SubjectService {
    public SubjectDetails addSubject(SubjectDetails subjectDetails, String schoolCode, MultipartFile file) throws Exception;
    public SubjectDetails getSubjectDetailsById(int subjectId, String schoolCode) throws Exception;
    public List<SubjectDetails> getAllSubjectDetails(String schoolCode) throws Exception;
    public SubjectDetails updateSubjectDetailsById(SubjectDetails subjectDetails, int subjectId,String schoolCode, MultipartFile file) throws Exception;
    public boolean deleteSubject(int subjectId, String schoolCode) throws Exception;
    public String softDeleteSubject(int subjectId, String schoolCode) throws Exception;
}
