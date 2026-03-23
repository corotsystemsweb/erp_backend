package com.sms.dao;

import com.sms.model.StaffAttendanceDetails;

import java.util.Date;
import java.util.List;

public interface StaffAttendanceDao {
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception;
    public List<StaffAttendanceDetails> getStaffAttendance(Integer staffId, Integer departmentId, Integer designationId, Date date, String schoolCode) throws Exception;
}
