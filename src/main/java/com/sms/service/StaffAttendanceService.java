package com.sms.service;

import com.sms.model.StaffAttendanceDetails;

import java.util.Date;
import java.util.List;

public interface StaffAttendanceService {
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception;
    public List<StaffAttendanceDetails> getStaffAttendance(Integer staffId, Integer departmentId, Integer designationId, Date date, String schoolCode) throws Exception;

}
