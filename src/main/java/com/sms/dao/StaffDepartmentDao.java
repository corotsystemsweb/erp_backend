package com.sms.dao;

import com.sms.model.StaffDepartment;

import java.util.List;

public interface StaffDepartmentDao {
    public StaffDepartment addStaffDepartment(StaffDepartment staffDepartment, String schoolCode) throws Exception;

    public StaffDepartment getStaffDepartmentById(int stDpId, String schoolCode) throws Exception;

    public List<StaffDepartment> getAllStaffDepartment(String schoolCode) throws Exception;

    public StaffDepartment updateStaffDepartment(StaffDepartment staffDepartment,int sdId,String schoolCode) throws Exception;

    public boolean deleteStaffDepartment(int stDpId, String schoolCode) throws Exception;
}
