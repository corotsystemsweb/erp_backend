package com.sms.service.impl;

import com.sms.model.FeeDepositDetails;
import com.sms.model.StudentDueFeeDetails;
import com.sms.model.StudentFeeCollectionDetails;
import com.sms.service.FeeDepositService;
import com.sms.service.StudentDueFeeService;
import com.sms.service.StudentFeeCollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class StudentDueFeeServiceImpl implements StudentDueFeeService {
   /* @Autowired
    private StudentFeeCollectionService studentFeeCollectionService;
    @Autowired
    private FeeDepositService feeDepositService;
    @Override
    public List<StudentDueFeeDetails> calculateDueFees(String schoolCode) throws Exception {
        List<StudentFeeCollectionDetails> feeCollections = studentFeeCollectionService.getAllStudentFeeCollection(schoolCode);
        List<FeeDepositDetails> feeDeposits = feeDepositService.getTotalAmountPaidByStudents(schoolCode);

        Map<Integer, Double> paymentMap = new HashMap<>();
        for(FeeDepositDetails deposit : feeDeposits){
            paymentMap.put(deposit.getStudentId(),deposit.getTotalAmountPaidByStudent());
        }
        List<StudentDueFeeDetails> dueFees = new ArrayList<>();
        for (StudentFeeCollectionDetails feeCollection : feeCollections) {
            double grossFee = feeCollection.getGrossStudentFee();
            double paidAmount = paymentMap.getOrDefault(feeCollection.getStudentId(), 0.0);
            double dueFee = grossFee - paidAmount;

            StudentDueFeeDetails dueFeeDetails = new StudentDueFeeDetails(feeCollection.getStudentId(),
                    feeCollection.getStudentName(),
                    feeCollection.getClassName(),
                    feeCollection.getSectionName(),
                    grossFee,
                    paidAmount,
                    dueFee);
            dueFees.add(dueFeeDetails);
        }
        return dueFees;
    }*/
   @Autowired
   private StudentFeeCollectionService studentFeeCollectionService;//gross fee
    @Autowired
    private FeeDepositService feeDepositService;

    @Override
    public List<StudentDueFeeDetails> calculateDueFees(String schoolCode) throws Exception {
        List<StudentFeeCollectionDetails> feeCollections = studentFeeCollectionService.getAllStudentFeeCollection(schoolCode);
        List<FeeDepositDetails> feeDeposits = feeDepositService.getTotalAmountPaidByStudents(schoolCode);

        Map<Integer, Double> paymentMap = new HashMap<>();
        for (FeeDepositDetails deposit : feeDeposits) {
            paymentMap.put(deposit.getStudentId(), deposit.getTotalAmountPaidByStudent());
        }

        List<StudentDueFeeDetails> dueFees = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Create SimpleDateFormat
        String currentDate = dateFormat.format(new Date());
        for (StudentFeeCollectionDetails feeCollection : feeCollections) {
            double grossFee = feeCollection.getGrossStudentFee();
            double paidAmount = paymentMap.getOrDefault(feeCollection.getStudentId(), 0.0);
            double dueFee = grossFee - paidAmount;

            // Create a new StudentDueFeeDetails object including the additional fields
            StudentDueFeeDetails dueFeeDetails = new StudentDueFeeDetails(
                    feeCollection.getSchoolId(),
                    feeCollection.getSchoolName(),
                    feeCollection.getSchoolBuilding(),
                    feeCollection.getSchoolAddress(),
                    feeCollection.getEmailAddress(),
                    feeCollection.getSchoolCity(),
                    feeCollection.getSchoolState(),
                    feeCollection.getSchoolCountry(),
                    feeCollection.getSchoolZipcode(),
                    feeCollection.getStudentId(),
                    feeCollection.getStudentName(),
                    feeCollection.getClassId(),
                    feeCollection.getClassName(),
                    feeCollection.getSectionId(),
                    feeCollection.getSectionName(),
                    grossFee,
                    paidAmount,
                    dueFee,
                    feeCollection.getSessionId(),
                    feeCollection.getSessionName(),
                    feeCollection.getFeeDetails(),
                    feeCollection.getTotalFeeAssigned(),
                    feeCollection.getTotalDiscount(),
                    currentDate
            );
            dueFees.add(dueFeeDetails);
        }
        return dueFees;
    }
    /*@Override
    public StudentDueFeeDetails calculateDueFeeBasedOnStudentId(int studentId, String schoolCode) throws Exception {
        StudentFeeCollectionDetails feeCollection = studentFeeCollectionService.getStudentFeeCollectionByStudentId(studentId, schoolCode);
        FeeDepositDetails feeDeposit = feeDepositService.getTotalAmountPaidByParticularStudent(studentId,schoolCode);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Create SimpleDateFormat
        String currentDate = dateFormat.format(new Date());

            double grossFee = feeCollection.getGrossStudentFee();
            double paidAmount = feeDeposit != null ? feeDeposit.getTotalAmountPaidByStudent() : 0.0;
            double dueFee = grossFee - paidAmount;

            // Create a new StudentDueFeeDetails object including the additional fields
            StudentDueFeeDetails dueFeeDetails = new StudentDueFeeDetails(
                    feeCollection.getSchoolId(),
                    feeCollection.getSchoolName(),
                    feeCollection.getStudentId(),
                    feeCollection.getStudentName(),
                    feeCollection.getClassName(),
                    feeCollection.getSectionName(),
                    grossFee,
                    paidAmount,
                    dueFee,
                    feeCollection.getSessionName(),
                    feeCollection.getFeeDetails(),
                    feeCollection.getTotalFeeAssigned(),
                    feeCollection.getTotalDiscount(),
                    currentDate
            );
        return dueFeeDetails;
    }
*/
    @Override
    public StudentDueFeeDetails calculateDueFeeBasedOnStudentId(int studentId, String schoolCode) throws Exception {
        // Retrieve fee collection details
        StudentFeeCollectionDetails feeCollection = studentFeeCollectionService.getStudentFeeCollectionByStudentId(studentId, schoolCode);

        // Retrieve fee deposit details including total_amount_paid_list
        FeeDepositDetails feeDeposit = feeDepositService.getTotalAmountPaidByParticularStudent(studentId, schoolCode);

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());

        double grossFee = feeCollection.getGrossStudentFee();
        double paidAmount = feeDeposit != null ? feeDeposit.getTotalAmountPaidByStudent() : 0.0;
        double dueFee = grossFee - paidAmount;

        // Create a new StudentDueFeeDetails object including the additional fields
        StudentDueFeeDetails dueFeeDetails = new StudentDueFeeDetails(
                feeCollection.getSchoolId(),
                feeCollection.getSchoolName(),
                feeCollection.getSchoolBuilding(),
                feeCollection.getSchoolAddress(),
                feeCollection.getEmailAddress(),
                feeCollection.getSchoolCity(),
                feeCollection.getSchoolState(),
                feeCollection.getSchoolCountry(),
                feeCollection.getSchoolZipcode(),
                feeCollection.getStudentId(),
                feeCollection.getStudentName(),
                feeCollection.getClassId(),
                feeCollection.getClassName(),
                feeCollection.getSectionId(),
                feeCollection.getSectionName(),
                grossFee,
                paidAmount,
                dueFee,
                feeCollection.getSessionId(),
                feeCollection.getSessionName(),
                feeCollection.getFeeDetails(),
                feeCollection.getTotalFeeAssigned(),
                feeCollection.getTotalDiscount(),
                currentDate
        );

        // Add total_amount_paid_list from FeeDepositDetails to StudentDueFeeDetails
        /*if (feeDeposit != null) {
            dueFeeDetails.setTotalAmountPaidList(feeDeposit.getTotalAmountPaidList());
        }*/
        if (paidAmount == 0.0) {
            dueFeeDetails.setTotalAmountPaidList(Collections.singletonList("Discount Amount : " + Collections.singletonList(String.valueOf(0.0))));
        } else {
            dueFeeDetails.setTotalAmountPaidList(feeDeposit.getTotalAmountPaidList());
        }

        return dueFeeDetails;
    }
    @Override
    public List<StudentDueFeeDetails> calculateDueFeesBySearchText(String searchText, String schoolCode) throws Exception {
        List<StudentFeeCollectionDetails> feeCollections = studentFeeCollectionService.getAllStudentFeeCollection(schoolCode);
        List<FeeDepositDetails> feeDeposits = feeDepositService.getTotalAmountPaidByStudents(schoolCode);

        Map<Integer, Double> paymentMap = new HashMap<>();
        for (FeeDepositDetails deposit : feeDeposits) {
            paymentMap.put(deposit.getStudentId(), deposit.getTotalAmountPaidByStudent());
        }

        List<StudentDueFeeDetails> dueFees = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = dateFormat.format(new Date());

        for (StudentFeeCollectionDetails feeCollection : feeCollections) {
            double grossFee = feeCollection.getGrossStudentFee();
            double paidAmount = paymentMap.getOrDefault(feeCollection.getStudentId(), 0.0);
            double dueFee = grossFee - paidAmount;

            StudentDueFeeDetails dueFeeDetails = new StudentDueFeeDetails(
                    feeCollection.getSchoolId(),
                    feeCollection.getSchoolName(),
                    feeCollection.getSchoolBuilding(),
                    feeCollection.getSchoolAddress(),
                    feeCollection.getEmailAddress(),
                    feeCollection.getSchoolCity(),
                    feeCollection.getSchoolState(),
                    feeCollection.getSchoolCountry(),
                    feeCollection.getSchoolZipcode(),
                    feeCollection.getStudentId(),
                    feeCollection.getStudentName(),
                    feeCollection.getClassId(),
                    feeCollection.getClassName(),
                    feeCollection.getSectionId(),
                    feeCollection.getSectionName(),
                    grossFee,
                    paidAmount,
                    dueFee,
                    feeCollection.getSessionId(),
                    feeCollection.getSessionName(),
                    feeCollection.getFeeDetails(),
                    feeCollection.getTotalFeeAssigned(),
                    feeCollection.getTotalDiscount(),
                    currentDate
            );

            // Set an empty totalAmountPaidList or use a default value
            if (paidAmount == 0.0) {
                dueFeeDetails.setTotalAmountPaidList(Collections.singletonList("Discount Amount : " + 0.0));
            } else {
                // If you want to add more meaningful data, you can customize this section
                dueFeeDetails.setTotalAmountPaidList(Collections.singletonList("Paid Amount: " + paidAmount));
            }

            // Concatenate the fields you want to search through
            String combinedText = String.join(" ",
                    //String.valueOf(dueFeeDetails.getStudentId()),
                    dueFeeDetails.getClassName(),
                    dueFeeDetails.getSectionName(),
                    dueFeeDetails.getStudentName(),
                    String.valueOf(grossFee),
                    String.valueOf(paidAmount),
                    String.valueOf(dueFee)
            );

            // Use ILIKE equivalent in Java
            if (combinedText.toLowerCase().contains(searchText.toLowerCase())) {
                dueFees.add(dueFeeDetails);
            }
        }
        // Return an empty list if no records found
        if (dueFees.isEmpty()) {
            return Collections.emptyList();
        }
        return dueFees;
    }

}
