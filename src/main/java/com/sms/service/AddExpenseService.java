package com.sms.service;

import com.sms.model.AddExpenseDetails;

import java.util.List;

public interface AddExpenseService {
    public AddExpenseDetails addExpenseDetails(AddExpenseDetails addExpenseDetails, String schoolCode) throws Exception;
    public AddExpenseDetails getAddExpenseDetailsById(int addExpenseId, String schoolCode) throws Exception;
    public List<AddExpenseDetails> getAllAddExpenseDetails(String schoolCode) throws Exception;
    public AddExpenseDetails updateAddExpenseDetails(AddExpenseDetails addExpenseDetails, int addExpenseId, String schoolCode) throws Exception;
    public boolean softDeleteAddExpenseDetails(int addExpenseId, String schoolCode) throws Exception;
    public List<AddExpenseDetails> getExpenseReport(String expenseTitle, String reportType, String schoolCode) throws Exception;
    public List<AddExpenseDetails> getExpenseDetailsBySearchText(String searchText, String schoolCode) throws Exception;
}
