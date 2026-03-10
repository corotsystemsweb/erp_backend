package com.sms.service.impl;

import com.sms.dao.ExamScheduleDao;
import com.sms.model.ExamScheduleDetails;
import com.sms.service.ExamScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamScheduleServiceImpl implements ExamScheduleService {

    @Autowired
    private ExamScheduleDao examScheduleDao;
    @Override
    public List<ExamScheduleDetails> addExamScheduleDetails(List<ExamScheduleDetails> examScheduleDetails, String schoolCode) throws Exception {
        return examScheduleDao.addExamScheduleDetails(examScheduleDetails,schoolCode);
    }

    @Override
    public ExamScheduleDetails addExam(ExamScheduleDetails examScheduleDetails, String schoolCode) throws Exception {
        return examScheduleDao.addExam(examScheduleDetails,schoolCode);
    }

    @Override
    public List<ExamScheduleDetails> getExamScheduleDetails(int sessionId, int classId, int sectionId, String examType, String schoolCode) throws Exception {
        return examScheduleDao.getExamScheduleDetails(sessionId,classId,sectionId,examType,schoolCode);
    }

    @Override
    public boolean softDeleteExamSchedule(int esId, String schoolCode) throws Exception {
        return examScheduleDao.softDeleteExamSchedule(esId,schoolCode);
    }

}
