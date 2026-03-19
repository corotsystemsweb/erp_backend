package com.sms.service;

import com.sms.model.CreateExamDetails;
import com.sms.model.ExamSubjectsDetails;

import java.util.List;

public interface CreateExamService {
    void createExamWithSubjects(CreateExamDetails createExamDetails,String schoolCode) throws Exception;
    List<CreateExamDetails> getAllExamDetails(int sessionId,String schoolCode,Integer classId, Integer sectionId, String examName);
    List<ExamSubjectsDetails> getExamTimeTable(int examId,String schoolCode);


}
