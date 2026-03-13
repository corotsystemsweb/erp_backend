package com.sms.service;

import com.sms.model.ExamResultDetails;

import java.util.List;

public interface ExamsResultService {
    List<ExamResultDetails> getExamResults(Integer sessionId, Integer classId, Integer sectionId,String schoolCode);
    List<ExamResultDetails>getExamResultWithSubject(Integer classId, Integer sectionId,String schoolCode);
    ExamResultDetails resultForParticularStudent(Integer classId,Integer sectionId,Integer studentId,Integer examTypeId,String schoolCode);

}
