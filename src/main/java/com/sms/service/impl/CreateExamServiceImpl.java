package com.sms.service.impl;

import com.sms.dao.CreateExamDao;
import com.sms.model.CreateExamDetails;
import com.sms.model.ExamSubjectsDetails;
import com.sms.service.CreateExamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

@Service
public class CreateExamServiceImpl implements CreateExamService {

    @Autowired
    private CreateExamDao createExamDao;

    @Override
    @Transactional
    public void createExamWithSubjects(CreateExamDetails createExamDetails,String schoolCode) {
        // Set automatic fields
        createExamDetails.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        createExamDetails.setStatus("Scheduled");

        // Save exam
        int examId = createExamDao.createExam(createExamDetails, schoolCode);
        System.out.println("examId"+examId);

        // Save subjects
        for (ExamSubjectsDetails subject : createExamDetails.getSubjects()) {
            subject.setExamId(examId);
            validateSubjectMarks(subject);
            createExamDao.createExamSubject(subject,schoolCode);
        }
    }

    @Override
    public List<CreateExamDetails> getAllExamDetails(int sessionId, String schoolCode,Integer classId, Integer sectionId,String examName) {
        return createExamDao.getAllExamDetails(sessionId,schoolCode,classId,sectionId,examName);
    }

    @Override
    public List<ExamSubjectsDetails> getExamTimeTable(int examId, String schoolCode) {
        return createExamDao.getExamTimeTable(examId,schoolCode);
    }

    private void validateSubjectMarks(ExamSubjectsDetails subject) {
        int total = subject.getTheoryMaxMarks() +
                subject.getPracticalMaxMarks() +
                subject.getVivaMaxMarks();

        if (subject.getPassingMarks() > total) {
            throw new IllegalArgumentException(
                    "Passing marks exceed total marks for subject " + subject.getSubjectId()
            );
        }
    }
}
