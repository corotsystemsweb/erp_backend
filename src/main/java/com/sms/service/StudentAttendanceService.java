package com.sms.service;

import com.sms.model.StudentAttendanceDetails;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StudentAttendanceService {
    public List<StudentAttendanceDetails> addAttendance(List<StudentAttendanceDetails> studentAttendanceDetails, String schoolCode) throws Exception;
    //public List<StudentAttendanceDetails> getTotalAttendance(int studentId, int classId, int sectionId, int subjectId, Date dateFrom, Date dateTo) throws Exception;
    public List<StudentAttendanceDetails> getTotalAttendance(Integer studentId,Integer subjectId, Date dateFrom, Date dateTo, String schoolCode) throws Exception;

}
