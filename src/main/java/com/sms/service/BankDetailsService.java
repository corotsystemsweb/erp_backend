package com.sms.service;

import com.sms.model.BankDetails;

import java.util.List;

public interface BankDetailsService {
    public BankDetails addBankDetails(BankDetails bankDetails, String schoolCode) throws Exception;
    public List<BankDetails> getAllStaffBankDetails(String schoolCode) throws Exception;

    public BankDetails getBankDetailsById(int bdId, String schoolCode) throws Exception;
    public BankDetails updateByEmpId(BankDetails bankDetails,int bdId, String schoolCode) throws Exception;

    public boolean softDeleteBankDetails(int bdId, String schoolCode) throws Exception;
   // public boolean deleteByEmpId(int bdId,String schoolCode) throws Exception;
   public List<BankDetails> getBankDetailsBySearchText(String searchText, String schoolCode) throws Exception;
}
