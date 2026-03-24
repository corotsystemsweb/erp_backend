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
    public List<StaffAttendanceDetails> addStaffAttendanceDetails(List<StaffAttendanceDetails> staffAttendanceDetails, String schoolCode) throws Exception {
        return staffAttendanceDao.addStaffAttendanceDetails(staffAttendanceDetails, schoolCode);
    }

    @Override
    public List<StaffAttendanceDetails> getStaffAttendance(Integer staffId, Integer departmentId, Integer designationId, Date date, String schoolCode) throws Exception {
        return staffAttendanceDao.getStaffAttendance(staffId, departmentId, designationId, date, schoolCode);
    }


}
