package com.sms.dao;

import com.sms.model.ExamScheduleDetails;

import java.util.List;

public interface ExamScheduleDao {
    public List<ExamScheduleDetails> addExamScheduleDetails(List<ExamScheduleDetails> examScheduleDetails,String schoolCode)throws  Exception;

    public ExamScheduleDetails addExam(ExamScheduleDetails examScheduleDetails, String schoolCode) throws Exception;

    public List<ExamScheduleDetails>getExamScheduleDetails(int sessionId,int classId,int sectionId,String examType,String schoolCode)throws  Exception;

    public List<ExamScheduleDetails>getAllExamScheduleDetails(int sessionId, String schoolCode) throws Exception;

   // public ExamScheduleDetails updateById(ExamScheduleDetails examScheduleDetails,int esId,String schoolCode)throws Exception;

    public boolean softDeleteExamSchedule(int esId, String schoolCode)throws  Exception;

}
