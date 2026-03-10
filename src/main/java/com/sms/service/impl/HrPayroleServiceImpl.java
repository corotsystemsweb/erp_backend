package com.sms.service.impl;

import com.sms.dao.HrPayroleDao;
import com.sms.model.HrPayroleDetails;
import com.sms.service.HrPayroleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HrPayroleServiceImpl implements HrPayroleService {

    @Autowired
    private HrPayroleDao hrPayroleDao;
    @Override
    public HrPayroleDetails addSalary(HrPayroleDetails hrPayroleDetails, String schoolCode) throws Exception {
        return hrPayroleDao.addSalary(hrPayroleDetails,schoolCode);
    }

    @Override
    public HrPayroleDetails getAllSalaryById(int ssId, String schoolCode) throws Exception {
        return hrPayroleDao.getAllSalaryById(ssId,schoolCode);
    }

    @Override
    public List<HrPayroleDetails> getAllSalary(String schoolCode) throws Exception {
        return hrPayroleDao.getAllSalary(schoolCode);
    }

    @Override
    public HrPayroleDetails updateSalaryDetails(HrPayroleDetails hrPayroleDetails, int ssId, String schoolCode) throws Exception {
        return hrPayroleDao.updateSalaryDetails(hrPayroleDetails,ssId,schoolCode);
    }

    @Override
    public boolean softDeletdSalary(int ssId, String schoolCode) throws Exception {
        return hrPayroleDao.softDeletdSalary(ssId,schoolCode);
    }

    @Override
    public List<HrPayroleDetails> getSalaryDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return hrPayroleDao.getSalaryDetailsBySearchText(searchText, schoolCode);
    }


}
