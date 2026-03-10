package com.sms.service;

import com.sms.model.ClassAttendanceDetails;

import java.sql.Date;

public interface ClassAttendanceService {
    public ClassAttendanceDetails addClassAttendance(ClassAttendanceDetails classAttendanceDetails, String schoolCode) throws Exception;
    public int getClassAttendanceId(int classId,int sectionId,int subjectId,int teacherId,Date presentDate, String schoolCode)throws Exception;

}
