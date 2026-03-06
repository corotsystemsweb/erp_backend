package com.sms.service;

import com.sms.model.ExpenseType;

import java.util.List;

public interface ExpenseTypeService {
    public ExpenseType addExpenseType(ExpenseType expenseType, String schoolCode) throws Exception;
    public ExpenseType getExpenseTypeById(int expenseTypeId, String schoolCode) throws Exception;
    public List<ExpenseType> getAllExpenseType(String schoolCode) throws Exception;
    public ExpenseType updateExpenseType(ExpenseType expenseType, int expenseTypeId, String schoolCode) throws Exception;
    public boolean softDeleteExpenseType(int expenseTypeId, String schoolCode) throws Exception;
    public List<ExpenseType> getExpenseTypeBySearchText(String searchText, String schoolCode) throws Exception;
}
