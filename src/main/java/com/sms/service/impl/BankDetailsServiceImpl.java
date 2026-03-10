package com.sms.service.impl;

import com.sms.dao.BankDetailsDao;
import com.sms.model.BankDetails;
import com.sms.service.BankDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankDetailsServiceImpl implements BankDetailsService {
    @Autowired
    private BankDetailsDao bankDetailsDao;
    @Override
    public BankDetails addBankDetails(BankDetails bankDetails, String schoolCode) throws Exception {

        return bankDetailsDao.addBankDetails(bankDetails,schoolCode);
    }

    @Override
    public List<BankDetails> getAllStaffBankDetails(String schoolCode) throws Exception {
        return bankDetailsDao.getAllStaffBankDetails(schoolCode);
    }

    @Override
    public BankDetails getBankDetailsById(int bdId, String schoolCode) throws Exception {
        return bankDetailsDao.getBankDetailsById(bdId,schoolCode);
    }

    @Override
    public BankDetails updateByEmpId(BankDetails bankDetails, int bdId, String schoolCode) throws Exception {
        return bankDetailsDao.updateByEmpId(bankDetails,bdId,schoolCode);
    }

    @Override
    public boolean softDeleteBankDetails(int bdId, String schoolCode) throws Exception {
        return bankDetailsDao.softDeleteBankDetails(bdId,schoolCode);
    }

    @Override
    public List<BankDetails> getBankDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return bankDetailsDao.getBankDetailsBySearchText(searchText, schoolCode);
    }

   /* @Override
    public boolean deleteByEmpId(int employeeId) throws Exception {
        return bankDetailsDao.deleteByEmpId(employeeId);
    }*/
}
