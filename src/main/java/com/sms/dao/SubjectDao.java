package com.sms.dao;

import com.sms.model.SubjectDetails;

import java.util.List;
import java.util.Map;

public interface SubjectDao {
    public SubjectDetails addSubject(SubjectDetails subjectDetails, String schoolCode) throws Exception;
    public SubjectDetails getSubjectDetailsById(int subjectId, String schoolCode) throws Exception;
    public List<SubjectDetails> getAllSubjectDetails(String schoolCode) throws Exception;
    public SubjectDetails updateSubjectDetailsById(SubjectDetails subjectDetails, int subjectId, String schoolCode) throws Exception;
    public boolean deleteSubject(int subjectId, String schoolCode) throws Exception;
}
