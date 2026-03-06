package com.sms.service.impl;

import com.sms.dao.FuelExpenseDao;
import com.sms.model.FuelExpenseDetails;
import com.sms.service.FuelExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FuelExpenseServiceImpl implements FuelExpenseService {
    @Autowired
    private FuelExpenseDao fuelExpenseDao;
    @Override
    public FuelExpenseDetails addFuelExpense(FuelExpenseDetails fuelExpenseDetails, String schoolCode) throws Exception {
        return fuelExpenseDao.addFuelExpense(fuelExpenseDetails, schoolCode);
    }

    @Override
    public FuelExpenseDetails getFuelExpenseById(int fuelExpenseId, String schoolCode) throws Exception {
        return fuelExpenseDao.getFuelExpenseById(fuelExpenseId, schoolCode);
    }

    @Override
    public List<FuelExpenseDetails> getAllFuelExpense(String schoolCode) throws Exception {
        return fuelExpenseDao.getAllFuelExpense(schoolCode);
    }

    @Override
    public FuelExpenseDetails updateFuelExpense(FuelExpenseDetails fuelExpenseDetails, int fuelExpenseId, String schoolCode) throws Exception {
        return fuelExpenseDao.updateFuelExpense(fuelExpenseDetails, fuelExpenseId, schoolCode);
    }

    @Override
    public boolean deleteFuelExpense(int fuelExpenseId, String schoolCode) throws Exception {
        return fuelExpenseDao.deleteFuelExpense(fuelExpenseId, schoolCode);
    }

    @Override
    public List<FuelExpenseDetails> getFuelExpenseReport(String reportType, String schoolCode) throws Exception {
        return fuelExpenseDao.getFuelExpenseReport(reportType, schoolCode);
    }

    @Override
    public List<FuelExpenseDetails> getFuelExpenseReportBySearchText(String searchText, String schoolCode) throws Exception {
        return fuelExpenseDao.getFuelExpenseReportBySearchText(searchText, schoolCode);
    }
}
