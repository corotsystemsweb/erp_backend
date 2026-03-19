package com.sms.service;

import com.sms.model.ExamResult;

import java.util.List;

public interface ExamResultService {
    void saveMarks(List<ExamResult> results,String schoolCode) throws Exception;
//    List<ExamResult> getMarksByExamSubject(Long examSubjectId);
}
