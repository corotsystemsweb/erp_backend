package com.sms.dao;

import com.sms.model.FuelExpenseDetails;

import java.util.List;

public interface FuelExpenseDao {
    public FuelExpenseDetails addFuelExpense(FuelExpenseDetails fuelExpenseDetails, String schoolCode) throws Exception;
    public FuelExpenseDetails getFuelExpenseById(int fuelExpenseId, String schoolCode) throws Exception;
    public List<FuelExpenseDetails> getAllFuelExpense(String schoolCode) throws Exception;
    public FuelExpenseDetails updateFuelExpense(FuelExpenseDetails fuelExpenseDetails, int fuelExpenseId, String schoolCode) throws Exception;
    public boolean deleteFuelExpense(int fuelExpenseId, String schoolCode) throws Exception;
    public List<FuelExpenseDetails> getFuelExpenseReport(String reportType, String schoolCode) throws Exception;
    public List<FuelExpenseDetails> getFuelExpenseReportBySearchText(String searchText, String schoolCode) throws Exception;
}
