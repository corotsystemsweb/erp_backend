package com.sms.service;

import com.sms.model.ExamMeetingDetails;

import java.util.List;

public interface ExamMeetingService {
    public ExamMeetingDetails addExamMeeting(ExamMeetingDetails examMeetingDetails, String schoolCode) throws  Exception;
    public  ExamMeetingDetails getExamMeetingById(int emId, String schoolCode) throws  Exception;
    public List<ExamMeetingDetails> getAllExamMeeting(String schoolCode) throws  Exception;
    public  ExamMeetingDetails updateExamMeeting(ExamMeetingDetails examMeetingDetails,int emId,String schoolCode) throws Exception;
    public boolean softDeleteExamMeeting(int emId, String schoolCode) throws  Exception;
    public List<ExamMeetingDetails> getExamMeetingDetailsBySearchText(String searchText, String schoolCode) throws Exception;
}
