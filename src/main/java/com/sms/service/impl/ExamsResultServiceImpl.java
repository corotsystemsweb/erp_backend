package com.sms.service.impl;

import com.sms.dao.ExamsResultDao;
import com.sms.model.ExamResultDetails;
import com.sms.service.ExamsResultService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamsResultServiceImpl implements ExamsResultService {

    private final ExamsResultDao examsResultDao;

    public ExamsResultServiceImpl(ExamsResultDao examsResultDao) {
        this.examsResultDao = examsResultDao;
    }

    @Override
    public List<ExamResultDetails> getExamResults(Integer sessionId, Integer classId, Integer sectionId,String schoolCode) {
        return examsResultDao.findAllStudentExamResults(sessionId,classId,sectionId,schoolCode);
    }

    @Override
    public List<ExamResultDetails> getExamResultWithSubject(Integer classId, Integer sectionId, String schoolCode) {
        return examsResultDao.findStudentResultWithSubject(classId,sectionId,schoolCode);
    }

    @Override
    public ExamResultDetails resultForParticularStudent(Integer classId, Integer sectionId, Integer studentId, Integer examTypeId, String schoolCode) {
        return examsResultDao.resultForParticularStudent(classId,sectionId,studentId,examTypeId,schoolCode);
    }
}
