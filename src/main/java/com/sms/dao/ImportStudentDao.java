package com.sms.dao;

import com.sms.model.ImportStudent;

import java.util.List;

public interface ImportStudentDao {
    List<ImportStudent>getStudentDetails(Integer sessionId,Integer classId,Integer sectionId,String schoolCode) throws Exception;
    boolean existsInNextSession(Integer studentId, Integer nextSessionId,String schoolCode);
    void validateSessionClassSection(Integer sessionId, Integer classId, Integer sectionId,String schoolCode);
    void promoteStudents(List<Integer> studentIds, Integer currentSessionId,
                         Integer nextSessionId, Integer nextClassId,
                         Integer nextSectionId,String schoolCode);
    List<ImportStudent> getGraduationEligibleStudents(Integer sessionId,Integer classId,Integer sectionId, String schoolCode);
//    boolean isHighestClass(Integer classId, String schoolCode);
    void graduateStudents(List<Integer> studentIds, Integer currentSessionId, String schoolCode);
}
