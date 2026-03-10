package com.sms.dao;

import com.sms.model.CreateExamDetails;
import com.sms.model.ExamSubjectsDetails;
import java.util.List;

public interface CreateExamDao {
    int createExam(CreateExamDetails exam,String schoolCode);
    void createExamSubject(ExamSubjectsDetails subject,String schoolCode);
    List<CreateExamDetails> getAllExamDetails(int sessionId,String schoolCode, Integer classId, Integer sectionId, String examName);
    List<ExamSubjectsDetails> getExamTimeTable(int examId,String schoolCode);
}

