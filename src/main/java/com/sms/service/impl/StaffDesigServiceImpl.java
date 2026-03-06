package com.sms.service.impl;

import com.sms.dao.StaffDesigDao;
import com.sms.model.StaffDesig;
import com.sms.service.StaffDesigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffDesigServiceImpl implements StaffDesigService {

    @Autowired
    private StaffDesigDao staffDesigDao;
    @Override
    public StaffDesig addStaffDesig(StaffDesig staffDesig, String schoolCode) throws Exception {
        return staffDesigDao.addStaffDesig(staffDesig, schoolCode);
    }
    @Override
    public StaffDesig getStaffDesignById(int sdId, String schoolCode) throws Exception {
        return staffDesigDao.getStaffDesignById(sdId, schoolCode);
    }

    @Override
    public List<StaffDesig> getAllStaffDesig(String schoolCode) throws Exception {
        return staffDesigDao.getAllStaffDesig(schoolCode);
    }

    @Override
    public StaffDesig updateStaffDesig(StaffDesig staffDesig, int sdId, String schoolCode) throws Exception {
        return staffDesigDao.updateStaffDesig(staffDesig,sdId, schoolCode);
    }

    @Override
    public boolean deleteStaffDesig(int sdId, String schoolCode) throws Exception {
        return staffDesigDao.deleteStaffDesig(sdId, schoolCode);
    }


}
