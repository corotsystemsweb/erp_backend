package com.sms.service.impl;

import com.sms.dao.MarkSheetDao;
import com.sms.model.MarkSheetDetails;
import com.sms.service.MarkSheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MarkSheetServiceImpl implements MarkSheetService {
    @Autowired
    private MarkSheetDao markSheetDao;

    @Override
    public List<MarkSheetDetails> searchAllStudentMarks(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        return markSheetDao.searchAllStudentMarks(classId, sectionId, sessionId, schoolCode);
    }
}
