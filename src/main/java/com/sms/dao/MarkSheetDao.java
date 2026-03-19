package com.sms.dao;

import com.sms.model.MarkSheetDetails;

import java.util.List;

public interface MarkSheetDao {
    public List<MarkSheetDetails> searchAllStudentMarks(int classId, int sectionId, int sessionId, String schoolCode) throws Exception;
}
