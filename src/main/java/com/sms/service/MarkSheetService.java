package com.sms.service;

import com.sms.dao.MarkSheetDao;
import com.sms.model.MarkSheetDetails;

import java.util.List;

public interface MarkSheetService {
    public List<MarkSheetDetails> searchAllStudentMarks(int classId, int sectionId, int sessionId, String schoolCode) throws Exception;

}
