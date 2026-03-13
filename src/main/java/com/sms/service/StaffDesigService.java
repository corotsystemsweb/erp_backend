package com.sms.service;

import com.sms.model.StaffDesig;

import java.util.List;

public interface StaffDesigService {
    public StaffDesig addStaffDesig(StaffDesig staffDesig, String schoolCode) throws Exception;
    public StaffDesig getStaffDesignById(int sdId, String schoolCode) throws Exception;
    public List<StaffDesig> getAllStaffDesig(String schoolCode) throws Exception;

    public StaffDesig updateStaffDesig(StaffDesig staffDesig,int sdId, String schoolCode) throws Exception;

    public boolean deleteStaffDesig(int sdId, String schoolCode) throws Exception;

}
