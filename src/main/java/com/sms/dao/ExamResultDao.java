package com.sms.dao;

import com.sms.model.ExamResult;

import java.util.List;

public interface ExamResultDao {
    public void saveMarks(List<ExamResult> results,String schoolCode) throws Exception;
}
