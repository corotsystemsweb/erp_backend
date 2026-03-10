package com.sms.service.impl;

import com.sms.dao.AddExpenseDao;
import com.sms.model.AddExpenseDetails;
import com.sms.service.AddExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddExpenseServiceImpl implements AddExpenseService {
    @Autowired
    private AddExpenseDao addExpenseDao;

    @Override
    public AddExpenseDetails addExpenseDetails(AddExpenseDetails addExpenseDetails, String schoolCode) throws Exception {
        return addExpenseDao.addExpenseDetails(addExpenseDetails, schoolCode);
    }

    @Override
    public AddExpenseDetails getAddExpenseDetailsById(int addExpenseId, String schoolCode) throws Exception {
        return addExpenseDao.getAddExpenseDetailsById(addExpenseId, schoolCode);
    }

    @Override
    public List<AddExpenseDetails> getAllAddExpenseDetails(String schoolCode) throws Exception {
        return addExpenseDao.getAllAddExpenseDetails(schoolCode);
    }

    @Override
    public AddExpenseDetails updateAddExpenseDetails(AddExpenseDetails addExpenseDetails, int addExpenseId, String schoolCode) throws Exception {
        return addExpenseDao.updateAddExpenseDetails(addExpenseDetails, addExpenseId, schoolCode);
    }

    @Override
    public boolean softDeleteAddExpenseDetails(int addExpenseId, String schoolCode) throws Exception {
        return addExpenseDao.softDeleteAddExpenseDetails(addExpenseId, schoolCode);
    }

    @Override
    public List<AddExpenseDetails> getExpenseReport(String expenseTitle, String reportType, String schoolCode) throws Exception {
        return addExpenseDao.getExpenseReport(expenseTitle, reportType, schoolCode);
    }

    @Override
    public List<AddExpenseDetails> getExpenseDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return addExpenseDao.getExpenseDetailsBySearchText(searchText, schoolCode);
    }
}
