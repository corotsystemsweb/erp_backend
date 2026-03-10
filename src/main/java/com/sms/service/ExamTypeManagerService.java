package com.sms.service;

import com.sms.model.ExamTypeManager;

import java.util.List;

public interface ExamTypeManagerService {
    ExamTypeManager addExamType(ExamTypeManager examTypeManager, String schoolCode) throws Exception;
    List<ExamTypeManager> getAllExamType(int sessionId, String schoolCode) throws Exception;
    ExamTypeManager getExamTypeById(int examTypeId,String schoolCode) throws Exception;
    ExamTypeManager updateById(ExamTypeManager examTypeManager,int examTypeId,String schoolCode) throws Exception;
}
