package com.sms.service.impl;

import com.sms.dao.PaySalaryDao;
import com.sms.model.PaySalaryDetails;
import com.sms.service.PaySalaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaySalaryServiceImpl implements PaySalaryService {
    @Autowired
    private PaySalaryDao paySalaryDao;
    @Override
    public PaySalaryDetails addCalculatedSalary(PaySalaryDetails paySalaryDetails, String schoolCode) throws Exception {
        return paySalaryDao.addCalculatedSalary(paySalaryDetails,schoolCode);
    }
    @Override
    public String payableSalary(String salaryAmount, String leaveCount) throws Exception {
        try {
            int salary = Integer.parseInt(salaryAmount);
            int leaves = Integer.parseInt(leaveCount);
            int leaveAmount = (salary / 30) * leaves;
            int data = salary - leaveAmount;
            String payableAmount = Integer.toString(data);
            return payableAmount;
        } catch (NumberFormatException e) {
            throw new Exception("Invalid input: please provide numeric values for salaryAmount and leaveCount.");
        }
    }

    @Override
    public List<PaySalaryDetails> findSalaryDetailsByMonth(String paySalaryMonth, String paySalaryYear, String schoolCode) throws Exception {
        return paySalaryDao.findSalaryDetailsByMonth(paySalaryMonth,paySalaryYear,schoolCode);
    }

    @Override
    public PaySalaryDetails findSalaryDetailsByStaffId(int staffId, String paySalaryMonth, String paySalaryYear, int designationId, String schoolCode) throws Exception {
        return paySalaryDao.findSalaryDetailsByStaffId(staffId,paySalaryMonth,paySalaryYear,designationId,schoolCode);
    }
}
