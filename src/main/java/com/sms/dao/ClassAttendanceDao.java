package com.sms.dao;


import com.sms.model.ClassAttendanceDetails;

import java.sql.Date;

public interface ClassAttendanceDao {
    public ClassAttendanceDetails addClassAttendance(ClassAttendanceDetails classAttendanceDetails, String schoolCode) throws Exception;

    public int getClassAttendanceId(int classId,int sectionId,int subjectId,int teacherId,Date presentDate, String schoolCode)throws Exception;
}
