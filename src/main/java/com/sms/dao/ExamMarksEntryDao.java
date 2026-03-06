package com.sms.dao;

import com.sms.model.ExamMarksEntryDetails;

import java.util.List;

public interface ExamMarksEntryDao {
    public ExamMarksEntryDetails addExamMarks(ExamMarksEntryDetails examMarksEntryDetails, String schoolCode) throws Exception;
    public ExamMarksEntryDetails getExamMarksById(int emeId, String schoolCode) throws Exception;
    public List<ExamMarksEntryDetails> getAllExamMarks(String schoolCode) throws Exception;
    public ExamMarksEntryDetails updateExamMarks(ExamMarksEntryDetails examMarksEntryDetails, int emeId, String schoolCode) throws Exception;
    public boolean deleteExamMarks(int emeId, String schoolCode) throws Exception;
}
