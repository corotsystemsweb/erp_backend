package com.sms.dao;

import com.sms.model.ExamResultDetails;

import java.util.List;

public interface ExamsResultDao {
    List<ExamResultDetails> findAllStudentExamResults(Integer sessionId, Integer classId, Integer sectionId,String schoolCode);
    List<ExamResultDetails> findStudentResultWithSubject(Integer classId,Integer sectionId,String schoolCode);
    ExamResultDetails resultForParticularStudent(Integer classId,Integer sectionId,Integer studentId,Integer examTypeId,String schoolCode);
}
