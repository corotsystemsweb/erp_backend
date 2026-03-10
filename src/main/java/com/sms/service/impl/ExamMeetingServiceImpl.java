package com.sms.service.impl;

import com.sms.dao.ExamMeetingDao;
import com.sms.model.ExamMeetingDetails;
import com.sms.service.ExamMeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamMeetingServiceImpl implements ExamMeetingService {
    @Autowired
    private ExamMeetingDao examMeetingDao;
    @Override
    public ExamMeetingDetails addExamMeeting(ExamMeetingDetails examMeetingDetails, String schoolCode) throws Exception {
        return examMeetingDao.addExamMeeting(examMeetingDetails,schoolCode);
    }
    @Override
    public ExamMeetingDetails getExamMeetingById(int emId, String schoolCode) throws Exception {
        return examMeetingDao.getExamMeetingById(emId,schoolCode);
    }

    @Override
    public List<ExamMeetingDetails> getAllExamMeeting(String schoolCode) throws Exception {
        return examMeetingDao.getAllExamMeeting(schoolCode);
    }

    @Override
    public ExamMeetingDetails updateExamMeeting(ExamMeetingDetails examMeetingDetails, int emId, String schoolCode) throws Exception {
        return examMeetingDao.updateExamMeeting(examMeetingDetails,emId,schoolCode);
    }

    @Override
    public boolean softDeleteExamMeeting(int emId, String schoolCode) throws Exception {
        return examMeetingDao.softDeleteExamMeeting(emId,schoolCode);
    }

    @Override
    public List<ExamMeetingDetails> getExamMeetingDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return examMeetingDao.getExamMeetingDetailsBySearchText(searchText, schoolCode);
    }
}
