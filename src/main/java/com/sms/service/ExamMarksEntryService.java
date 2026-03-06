package com.sms.service;

import com.sms.model.ExamMarksEntryDetails;

import java.util.List;

public interface ExamMarksEntryService {
    public ExamMarksEntryDetails addExamMarks(ExamMarksEntryDetails examMarksEntryDetails, String schoolCode) throws Exception;
    public ExamMarksEntryDetails getExamMarksById(int emeId, String schoolCode) throws Exception;
    public List<ExamMarksEntryDetails> getAllExamMarks(String schoolCode) throws Exception;
    public ExamMarksEntryDetails updateExamMarks(ExamMarksEntryDetails examMarksEntryDetails, int emeId, String schoolCode) throws Exception;
    public boolean deleteExamMarks(int emeId, String schoolCode) throws Exception;
}
