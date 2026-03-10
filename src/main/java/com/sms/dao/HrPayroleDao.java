package com.sms.dao;

import com.sms.model.HrPayroleDetails;

import java.util.List;

public interface HrPayroleDao{
    public HrPayroleDetails addSalary(HrPayroleDetails hrPayroleDetails, String schoolCode) throws Exception;
    public HrPayroleDetails getAllSalaryById(int ssId,String schoolCode) throws  Exception;
    public List<HrPayroleDetails> getAllSalary(String schoolCode)throws  Exception;

    public HrPayroleDetails updateSalaryDetails(HrPayroleDetails hrPayroleDetails, int ssId, String schoolCode) throws Exception;

    public boolean softDeletdSalary(int ssId, String schoolCode) throws  Exception;
    public List<HrPayroleDetails> getSalaryDetailsBySearchText(String searchText, String schoolCode) throws Exception;
    HrPayroleDetails getSalaryByStaffId(int staffId, String schoolCode) throws Exception;
}
