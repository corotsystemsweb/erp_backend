package com.sms.service.impl;

import com.sms.dao.StaffAttendanceDao;
import com.sms.model.StaffAttendanceDetails;
import com.sms.service.StaffAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class StaffAttendanceServiceImpl implements StaffAttendanceService {
    @Autowired
    private StaffAttendanceDao staffAttendanceDao;

    @Override
    public List<StaffAttendanceDetails> getAllStaffDetails(String schoolCode) throws Exception {
        return staffAttendanceDao.getAllStaffDetails(schoolCode);
    }

    @Override
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception {
        return staffAttendanceDao.addStaffAttendanceDetails(staffAttendanceDetails, schoolCode);
    }

    @Override
    public List<StaffAttendanceDetails> getAllStaffAttendanceDetails(Integer staffId, String staffName, Integer designationId, Date dateFrom, Date dateTo, String schoolCode) throws Exception {
        return staffAttendanceDao.getAllStaffAttendanceDetails(staffId, staffName, designationId, dateFrom, dateTo, schoolCode);
    }
}
