package com.sms.service.impl;

import com.sms.dao.ExamMarksEntryDao;
import com.sms.model.ExamMarksEntryDetails;
import com.sms.service.ExamMarksEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamMarksEntryServiceImpl implements ExamMarksEntryService {
    @Autowired
    private ExamMarksEntryDao examMarksEntryDao;

    @Override
    public ExamMarksEntryDetails addExamMarks(ExamMarksEntryDetails examMarksEntryDetails, String schoolCode) throws Exception {
        return examMarksEntryDao.addExamMarks(examMarksEntryDetails, schoolCode);
    }

    @Override
    public ExamMarksEntryDetails getExamMarksById(int emeId, String schoolCode) throws Exception {
        return examMarksEntryDao.getExamMarksById(emeId, schoolCode);
    }

    @Override
    public List<ExamMarksEntryDetails> getAllExamMarks(String schoolCode) throws Exception {
        return examMarksEntryDao.getAllExamMarks(schoolCode);
    }

    @Override
    public ExamMarksEntryDetails updateExamMarks(ExamMarksEntryDetails examMarksEntryDetails, int emeId, String schoolCode) throws Exception {
        return examMarksEntryDao.updateExamMarks(examMarksEntryDetails, emeId, schoolCode);
    }

    @Override
    public boolean deleteExamMarks(int emeId, String schoolCode) throws Exception {
        return examMarksEntryDao.deleteExamMarks(emeId, schoolCode);
    }
}
