package com.sms.service.impl;

import com.sms.dao.AccountantDao;
import com.sms.model.AccountantDetails;
import com.sms.service.AccountantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccountantServiceImpl implements AccountantService {
    @Autowired
    private AccountantDao accountantDao;
    @Override
    public boolean addAccount(AccountantDetails accountantDetails) throws Exception {
        return accountantDao.addAccount(accountantDetails);
    }

    @Override
    public AccountantDetails getAccountantDetailsById(int id) throws Exception {
        return accountantDao.getAccountantDetailsById(id);
    }

    @Override
    public List<AccountantDetails> getAllAccountantDetails() throws Exception {
        return accountantDao.getAllAccountantDetails();
    }
}
