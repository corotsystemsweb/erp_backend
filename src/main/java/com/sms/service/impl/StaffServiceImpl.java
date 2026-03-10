package com.sms.service.impl;

import com.sms.dao.BankDetailsDao;
import com.sms.dao.HrPayroleDao;
import com.sms.dao.StaffDao;
import com.sms.model.BankDetails;
import com.sms.model.HrPayroleDetails;
import com.sms.model.StaffDetails;
import com.sms.service.StaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class StaffServiceImpl implements StaffService {

    @Autowired
    private final StaffDao staffDao;

    @Autowired
    private BankDetailsDao bankDetailsDao;

    @Autowired
    private HrPayroleDao hrPayroleDao;

    public StaffServiceImpl(StaffDao staffDao) {
        this.staffDao = staffDao;
    }

   /* @Override
    public boolean addImage(MultipartFile file, int staffId) throws Exception {
        return staffDao.addImage(file,staffId);
    }

    @Override
    public StaffDetails getImage(int staffId) throws Exception {
        return staffDao.getImage(staffId);
    }*/

    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int staffId) throws Exception {
        return staffDao.addImage(file, schoolCode, staffId);
    }

    @Override
    public StaffDetails getImage(String schoolCode, int staffId) throws Exception {
        return staffDao.getImage(schoolCode, staffId);
    }

    @Override
    public StaffDetails addStaffOneBYOne(StaffDetails staffDetails,String schoolCode) throws Exception {
        // 1 Save Staff
        StaffDetails savedStaff = staffDao.addStaffOneBYOne(staffDetails, schoolCode);

        if (savedStaff == null) {
            return null;
        }

        // 2 If bank details provided → save
        BankDetails bank = staffDetails.getBankDetails();
        if (isBankDetailsValid(bank)) {

            bank.setStaffId(savedStaff.getStaffId());
            bank.setSchoolId(1);
            bank.setStaffName(savedStaff.getFirstName() + " " + savedStaff.getLastName());
            bank.setPhoneNumber(staffDetails.getPhoneNumber());

            bankDetailsDao.addBankDetails(bank, schoolCode);
        }

        //3 Save Salary
        HrPayroleDetails salary = staffDetails.getHrPayroleDetails();
        if (isSalaryValid(salary)) {

            salary.setStaffId(savedStaff.getStaffId());
            salary.setSchoolId(1);
            salary.setSessionId(savedStaff.getSessionId());
            salary.setDepartmentId(savedStaff.getDepartmentId());
            salary.setDesignationId(savedStaff.getDesignationId());

            hrPayroleDao.addSalary(salary, schoolCode);
        }

        return savedStaff;
    }

    @Override
    public StaffDetails getStaffDetailsById(int staffId,String schoolCode) throws Exception {
        StaffDetails staff = staffDao.getStaffDetailsById(staffId, schoolCode);
        if (staff == null) return null;
        BankDetails bank = bankDetailsDao.getBankDetailsByStaffId(staffId, schoolCode);
        if (bank != null) {
            staff.setBankDetails(bank);
        }

        return staff;
    }
//    @Override
//    public List<StaffDetails> getAllStaffDetails(String schoolCode) throws Exception {
//        List<StaffDetails> staffDetailsList = staffDao.getAllStaffDetails(schoolCode);
//        if (staffDetailsList != null && !staffDetailsList.isEmpty()) {
//            for (StaffDetails sd : staffDetailsList) {
//                try {
//                    // Attempt to fetch the image for the current staff
//                    StaffDetails staffDetailsWithImage = staffDao.getImage(schoolCode, sd.getStaffId());
//                    if (staffDetailsWithImage != null) {
//                        sd.setStaffImage(staffDetailsWithImage.getStaffImage());
//                    }
//                } catch (IOException e) {
//                    // If the image is not found, simply ignore and return employee details without image
//                    //Do not throw exception, just skip setting the image
//                    //Logging the error is optional
//                    //e.printStackTrace(); // optional: Only If I want to log
//                }
//            }
//        }
//        return staffDetailsList;
//    }

    @Override
    public List<StaffDetails> getAllStaffDetails(String type, String schoolCode) throws Exception {

        List<StaffDetails> staffDetailsList = staffDao.getAllStaffDetails(type, schoolCode);

        if (staffDetailsList != null && !staffDetailsList.isEmpty()) {

            for (StaffDetails sd : staffDetailsList) {

                // Load Image
                try {
                    StaffDetails staffDetailsWithImage = staffDao.getImage(schoolCode, sd.getStaffId());

                    if (staffDetailsWithImage != null) {
                        sd.setStaffImage(staffDetailsWithImage.getStaffImage());
                    }

                } catch (IOException e) {
                    // Ignore if image not found
                }

                // Load Bank Details
                try {
                    BankDetails bank = bankDetailsDao.getBankDetailsByStaffId(sd.getStaffId(), schoolCode);

                    if (bank != null) {
                        sd.setBankDetails(bank);
                    }

                } catch (Exception e) {
                    // Ignore if bank details not found
                }
            }
        }

        return staffDetailsList;
    }

    @Override
    public StaffDetails updateStaffById(StaffDetails staffDetails, int staffId, String schoolCode) throws Exception {

        StaffDetails updatedStaff = staffDao.updateStaffById(staffDetails, staffId, schoolCode);

        if (updatedStaff == null) return null;

        BankDetails incomingBank = staffDetails.getBankDetails();

        if (isBankDetailsValid(incomingBank)) {

            BankDetails existingBank = bankDetailsDao.getBankDetailsByStaffId(staffId, schoolCode);

            // Always sync these
            incomingBank.setStaffId(staffId);
            incomingBank.setSchoolId(1);
            incomingBank.setStaffName(updatedStaff.getFirstName() + " " + updatedStaff.getLastName());

            // Send decrypted phone → DAO will encrypt
            incomingBank.setPhoneNumber(updatedStaff.getPhoneNumber());

            if (existingBank != null) {
                bankDetailsDao.updateByEmpId(incomingBank, existingBank.getBdId(), schoolCode);
            } else {
                bankDetailsDao.addBankDetails(incomingBank, schoolCode);
            }
        }
        // 3 Update Salary (OPTIONAL)
        HrPayroleDetails incomingSalary = staffDetails.getHrPayroleDetails();

        if (isSalaryValid(incomingSalary)) {

            // First check if salary record already exists
            HrPayroleDetails existingSalary = hrPayroleDao.getSalaryByStaffId(staffId, schoolCode);

            incomingSalary.setStaffId(staffId);
            incomingSalary.setSchoolId(1);
            incomingSalary.setSessionId(updatedStaff.getSessionId());
            incomingSalary.setDepartmentId(updatedStaff.getDepartmentId());
            incomingSalary.setDesignationId(updatedStaff.getDesignationId());

            if (existingSalary != null) {
                // Update existing
                hrPayroleDao.updateSalaryDetails(incomingSalary, existingSalary.getSsId(), schoolCode);
            } else {
                // Insert new
                hrPayroleDao.addSalary(incomingSalary, schoolCode);
            }
        }

        return updatedStaff;
    }

    private boolean isBankDetailsValid(BankDetails bank) {
        if (bank == null) {
            return false;
        }

        if (bank.getAccountNumber() == null || bank.getAccountNumber().trim().isEmpty()) {
            return false;
        }

        if (bank.getBankName() == null || bank.getBankName().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private boolean isSalaryValid(HrPayroleDetails salary) {

        if (salary == null) {
            return false;
        }
        if (salary.getSalaryAmount() == null || salary.getSalaryAmount().trim().isEmpty()) {
            return false;
        }

        return true;
    }



    @Override
    public boolean softDeleteStaff(int staffId,String schoolCode) throws Exception {
        return staffDao.softDeleteStaff(staffId,schoolCode);
    }

    @Override
    public int getTotalStaff(String schoolCode) throws Exception {
        return staffDao.getTotalStaff(schoolCode);
    }


    @Override
    public List<StaffDetails> totalTeacher(String schoolCode,String staffType, String filter) throws Exception {
        try {
            // Pass schoolCode and filter to DAO
            return staffDao.totalTeacher(schoolCode, staffType, filter);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public StaffDetails getStaffId(String staffName,String schoolCode) throws Exception {
        return staffDao.getStaffId(staffName,schoolCode);
    }

    @Override
    public List<StaffDetails> getStaffByDesignation(int designationId, String schoolCode) throws Exception {
        return staffDao.getStaffByDesignation(designationId,schoolCode);
    }

    @Override
    public List<StaffDetails> getSalaryByDesignation(int designationId, String schoolCode) throws Exception {
        return staffDao.getSalaryByDesignation(designationId,schoolCode);
    }

    @Override
    public List<StaffDetails> getStaffDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return staffDao.getStaffDetailsBySearchText(searchText, schoolCode);
    }

    @Override
    public List<StaffDetails> getAllStaffForIdCardGeneration(String schoolCode) throws Exception {
        List<StaffDetails> staffDetailsList = staffDao.getAllStaffForIdCardGeneration(schoolCode);
        if (staffDetailsList != null && !staffDetailsList.isEmpty()) {
            for (StaffDetails sd : staffDetailsList) {
                try {
                    // Attempt to fetch the image for the current staff
                    StaffDetails staffDetailsWithImage = staffDao.getImage(schoolCode, sd.getStaffId());
                    if (staffDetailsWithImage != null) {
                        sd.setStaffImage(staffDetailsWithImage.getStaffImage());
                    }
                } catch (IOException e) {
                    // If the image is not found, simply ignore and return employee details without image
                    //Do not throw exception, just skip setting the image
                    //Logging the error is optional
                    //e.printStackTrace(); // optional: Only If I want to log
                }
            }
        }
        return staffDetailsList;
    }

    @Override
    public StaffDetails getStaffByIdForIdCardGeneration(int staffId, String schoolCode) throws Exception {
        StaffDetails staffDetails =  staffDao.getStaffByIdForIdCardGeneration(staffId, schoolCode);
        if(staffDetails != null){
            try{
                StaffDetails staffDetailsWithImage = staffDao.getImage(schoolCode, staffDetails.getStaffId());
                staffDetails.setStaffImage(staffDetailsWithImage.getStaffImage());
            }catch ( IOException e){
                // If the image is not found, simply ignore and return employee details without image
                //Do not throw exception, just skip setting the image
                //Logging the error is optional
                //e.printStackTrace(); // optional: Only If I want to log
            }
        }
        return staffDetails;
    }

}
