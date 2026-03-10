package com.sms.service.impl;

import com.sms.dao.StaffDepartmentDao;
import com.sms.model.StaffDepartment;
import com.sms.service.StaffDepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffDepartmentServiceImpl implements StaffDepartmentService {
    @Autowired
    private StaffDepartmentDao staffDepartmentDao;


    @Override
    public StaffDepartment addStaffDepartment(StaffDepartment staffDepartment, String schoolCode) throws Exception {
        return staffDepartmentDao.addStaffDepartment(staffDepartment, schoolCode);
    }

    @Override
    public StaffDepartment getStaffDepartmentById(int stDpId, String schoolCode) throws Exception {
        return staffDepartmentDao.getStaffDepartmentById(stDpId, schoolCode);
    }

    @Override
    public List<StaffDepartment> getAllStaffDepartment(String schoolCode) throws Exception {
        return staffDepartmentDao.getAllStaffDepartment(schoolCode);
    }

    @Override
    public StaffDepartment updateStaffDepartment(StaffDepartment staffDepartment, int sdId, String schoolCode) throws Exception {
        return staffDepartmentDao.updateStaffDepartment(staffDepartment, sdId, schoolCode);
    }

    @Override
    public boolean deleteStaffDepartment(int stDpId, String schoolCode) throws Exception {
        return staffDepartmentDao.deleteStaffDepartment(stDpId, schoolCode);
    }
}
