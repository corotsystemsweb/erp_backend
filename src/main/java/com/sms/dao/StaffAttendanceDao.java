package com.sms.dao;

import com.sms.model.StaffAttendanceDetails;

import java.util.Date;
import java.util.List;

public interface StaffAttendanceDao {
    public List<StaffAttendanceDetails> getAllStaffDetails(String schoolCode) throws Exception;
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception;
    public List<StaffAttendanceDetails> getAllStaffAttendanceDetails(Integer staffId, String staffName, Integer designationId, Date dateFrom, Date dateTo, String schoolCode) throws Exception;
}
