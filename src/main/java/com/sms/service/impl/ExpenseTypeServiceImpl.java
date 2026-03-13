package com.sms.service.impl;

import com.sms.dao.ExpenseTypeDao;
import com.sms.model.ExpenseType;
import com.sms.service.ExpenseTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseTypeServiceImpl implements ExpenseTypeService {
    @Autowired
    private ExpenseTypeDao expenseTypeDao;

    @Override
    public ExpenseType addExpenseType(ExpenseType expenseType, String schoolCode) throws Exception {
        return expenseTypeDao.addExpenseType(expenseType, schoolCode);
    }

    @Override
    public ExpenseType getExpenseTypeById(int expenseTypeId, String schoolCode) throws Exception {
        return expenseTypeDao.getExpenseTypeById(expenseTypeId, schoolCode);
    }

    @Override
    public List<ExpenseType> getAllExpenseType(String schoolCode) throws Exception {
        return expenseTypeDao.getAllExpenseType(schoolCode);
    }

    @Override
    public ExpenseType updateExpenseType(ExpenseType expenseType, int expenseTypeId, String schoolCode) throws Exception {
        return expenseTypeDao.updateExpenseType(expenseType, expenseTypeId, schoolCode);
    }

    @Override
    public boolean softDeleteExpenseType(int expenseTypeId, String schoolCode) throws Exception {
        return expenseTypeDao.softDeleteExpenseType(expenseTypeId, schoolCode);
    }

    @Override
    public List<ExpenseType> getExpenseTypeBySearchText(String searchText, String schoolCode) throws Exception {
        return expenseTypeDao.getExpenseTypeBySearchText(searchText, schoolCode);
    }
}
