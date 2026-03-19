package com.sms.service;

import com.sms.model.PaySalaryDetails;

import java.util.List;

public interface PaySalaryService {
    public PaySalaryDetails addCalculatedSalary(PaySalaryDetails paySalaryDetails, String schoolCode) throws Exception;
    public  String payableSalary(String salaryAmount, String leaveCount) throws  Exception;
    public List<PaySalaryDetails> findSalaryDetailsByMonth(String paySalaryMonth, String paySalaryYear, String schoolCode) throws Exception;
    public PaySalaryDetails findSalaryDetailsByStaffId(int staffId, String paySalaryMonth, String paySalaryYear, int designationId, String schoolCode) throws Exception;
}
