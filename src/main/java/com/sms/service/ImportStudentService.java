package com.sms.service;

import com.sms.model.ImportStudent;

import java.util.List;

public interface ImportStudentService {
    List<ImportStudent> getStudentDetails(Integer sessionId, Integer classId, Integer sectionId, String schoolCode) throws Exception;

    void promoteStudents(ImportStudent importStudent, Integer currentSessionId,String schoolCode);
    List<ImportStudent> getGraduationEligibleStudents(Integer sessionId,Integer classId,Integer sectionId, String schoolCode);
    void graduateStudents(ImportStudent importStudent, Integer currentSessionId, String schoolCode);

}
