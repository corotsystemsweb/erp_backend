package com.sms.service;

import com.sms.model.AccountantDetails;

import java.util.List;

public interface AccountantService {
    public boolean addAccount(AccountantDetails accountantDetails) throws Exception;
    public AccountantDetails getAccountantDetailsById(int id) throws Exception;
    public List<AccountantDetails> getAllAccountantDetails() throws Exception;
}
