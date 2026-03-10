package com.sms.dao;

import com.sms.model.AccountantDetails;

import java.util.List;

public interface AccountantDao {
    public boolean addAccount(AccountantDetails accountantDetails) throws Exception;
    public AccountantDetails getAccountantDetailsById(int id) throws Exception;
    public List<AccountantDetails> getAllAccountantDetails() throws Exception;
}
