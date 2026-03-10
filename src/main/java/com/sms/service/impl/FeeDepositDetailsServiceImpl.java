package com.sms.service.impl;

import com.sms.dao.*;
import com.sms.model.*;
import com.sms.service.FeeDepositDetailsService;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class FeeDepositDetailsServiceImpl implements FeeDepositDetailsService {
    @Autowired
    private FeeDepositDetailsDao feeDepositDetailsDao;
    @Autowired
    private FeeDepositDao feeDepositDao;
    @Autowired
    private StudentDao studentDao;

    @Autowired
    private SchoolDao schoolDao;

    @Autowired
    private TransportFeeDepositDao transportFeeDepositDao;

    @Autowired
    private TransportFeeDepositDetailsDao transportFeeDepositDetailsDao;

    /*   @Override
       @Transactional(rollbackFor = Exception.class)
       public List<FeeDepositDetails> addFeeDepositDetails(List<FeeDepositDetails> feeDepositDetails, String schoolCode) throws Exception {
           List<FeeDepositDetails> addedDetails = feeDepositDetailsDao.addFeeDepositDetails(feeDepositDetails, schoolCode);

           // Update status for each added FeeDepositDetails entry
           for(FeeDepositDetails detail : addedDetails){
               detail.setFddStatus("settled");
               feeDepositDao.updateStatus(detail, detail.getFdId(), schoolCode);
           }
           return addedDetails;
       }*/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CombinedFeeDepositResponse addFeeDepositDetails(FeeDepositRequest feeDepositRequest, String schoolCode) throws Exception {
        CombinedFeeDepositResponse response = new CombinedFeeDepositResponse();
        // Calculate the total amount
        double totalAmount = feeDepositRequest.getFeeDetails().stream()
                .mapToDouble(FeeDepositDetails::getAmountPaid)
                .sum();

        // Create a new FeeDeposit entry
        FeeDepositDetails feeDeposit = new FeeDepositDetails();
        feeDeposit.setSchoolId(feeDepositRequest.getSchoolId());
        feeDeposit.setSessionId(feeDepositRequest.getSessionId());
        feeDeposit.setClassId(feeDepositRequest.getClassId());
        feeDeposit.setSectionId(feeDepositRequest.getSectionId());
        feeDeposit.setStudentId(feeDepositRequest.getStudentId());
        feeDeposit.setPaymentMode(feeDepositRequest.getPaymentMode());
        feeDeposit.setTotalAmountPaid((int) totalAmount);
        feeDeposit.setPaymentReceivedBy(feeDepositRequest.getPaymentReceivedBy());
        feeDeposit.setSystemDateTime(new java.util.Date());
        feeDeposit.setPaymentDescription(feeDepositRequest.getPaymentDescription());
        feeDeposit.setTransactionId(feeDepositRequest.getTransactionId());
        feeDeposit.setComment(feeDepositRequest.getComment());
        feeDeposit.setFddStatus(feeDepositRequest.getStatus());
        feeDeposit.setPaymentDate(feeDepositRequest.getPaymentDate());
      //  feeDeposit.setAdditionalDiscount(feeDepositRequest.getAdditionalDiscount());
      //  feeDeposit.setAdditionalDiscountReason(feeDepositRequest.getAdditionalDiscountReason());
        // Insert into fee_deposit
        FeeDepositDetails insertedFeeDeposit = feeDepositDao.addFeeDeposit(feeDeposit, schoolCode);

        // Set the fd_id for each feeDepositDetails entry
        for (FeeDepositDetails detail : feeDepositRequest.getFeeDetails()) {
            detail.setFdId(insertedFeeDeposit.getFdId());
            detail.setApprovedBy(feeDepositRequest.getApprovedBy());
            detail.setPaymentReceivedBy(feeDepositRequest.getPaymentReceivedBy());
        }

        // Insert into fee_deposit_details
        List<FeeDepositDetails> addedDetails = feeDepositDetailsDao.addFeeDepositDetails(feeDepositRequest.getFeeDetails(), schoolCode);

        response.setAcademicFeeDetails(addedDetails);

        // 2 OPTIONAL TRANSPORT INSERT
        if(feeDepositRequest.getTransportFeeDepositDetails() != null && !feeDepositRequest.getTransportFeeDepositDetails().isEmpty()){
            double totalTransportAmount = feeDepositRequest.getTransportFeeDepositDetails().stream().mapToDouble(TransportFeeDepositDetails::getAmountPaid).sum();
            TransportFeeDepositDetails feeDepositDetails = new TransportFeeDepositDetails();
            feeDepositDetails.setSchoolId(feeDepositRequest.getSchoolId());
            feeDepositDetails.setSessionId(feeDepositRequest.getSessionId());
            feeDepositDetails.setClassId(feeDepositRequest.getClassId());
            feeDepositDetails.setSectionId(feeDepositRequest.getSectionId());
            feeDepositDetails.setStudentId(feeDepositRequest.getStudentId());
            feeDepositDetails.setRouteId(feeDepositRequest.getRouteId());
            feeDepositDetails.setPaymentMode(feeDepositRequest.getPaymentMode());
            feeDepositDetails.setTotalAmountPaid(totalTransportAmount);
            feeDepositDetails.setPaymentReceivedBy(feeDepositRequest.getPaymentReceivedBy());
            if (feeDepositRequest.getTransportPaymentDate() != null) {
                feeDepositDetails.setPaymentDate(
                        feeDepositRequest.getTransportPaymentDate()
                );
            } else {
                feeDepositDetails.setPaymentDate(LocalDateTime.now());
            }
            feeDepositDetails.setTransactionId(feeDepositRequest.getTransactionId());
            feeDepositDetails.setPaymentDescription(feeDepositRequest.getPaymentDescription());
            feeDepositDetails.setStatus(feeDepositRequest.getStatus());

            TransportFeeDepositDetails transportFeeDepositDetails = transportFeeDepositDao.addTransportFeeDeposit(feeDepositDetails, schoolCode);

            //Set generated tfd_id into each detail
            for (TransportFeeDepositDetails detail : feeDepositRequest.getTransportFeeDepositDetails()) {
                detail.setTfdId(transportFeeDepositDetails.getTfdId());
            }

            //Insert into Transport Fee Deposit Details
            List<TransportFeeDepositDetails> addDetails = transportFeeDepositDetailsDao.addTransportFeeDepositDetails(feeDepositRequest.getTransportFeeDepositDetails(), schoolCode);
            // Set Transport Response
            response.setTransportFeeDetails(addDetails);
        }
        return response;
    }

    @Override
    public List<FeeDepositDetails> getStudentFeeDetailsBasedOnClassSectionSession(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        List<FeeDepositDetails> feeDepositDetailsList = feeDepositDetailsDao.getStudentFeeDetailsBasedOnClassSectionSession(classId, sectionId, sessionId, schoolCode);
        if (feeDepositDetailsList != null && !feeDepositDetailsList.isEmpty()) {
            for (FeeDepositDetails fdd : feeDepositDetailsList) {
                try {
                    StudentDetails studentDetailsImage = studentDao.getImage(schoolCode, fdd.getStudentId());
                    if (studentDetailsImage != null) {
                        fdd.setStudentImage(studentDetailsImage.getStudentImage());
                    }
                } catch (IOException e) {
                    // Do not want to throw exception
                    //If the image is not found it just skip the image but other details will be come as response
                }
            }
        }
        return feeDepositDetailsList;
    }

    @Override
    public List<FeeDepositDetails> getAllStudentFeeDetails(int sessionId, String schoolCode) throws Exception {
        List<FeeDepositDetails> feeDepositDetailsList = feeDepositDetailsDao.getAllStudentFeeDetails(sessionId, schoolCode);
        if (feeDepositDetailsList != null && !feeDepositDetailsList.isEmpty()) {
            for (FeeDepositDetails fdd : feeDepositDetailsList) {
                try {
                    StudentDetails studentDetailsImage = studentDao.getImage(schoolCode, fdd.getStudentId());
                    if (studentDetailsImage != null) {
                        fdd.setStudentImage(studentDetailsImage.getStudentImage());
                    }
                } catch (IOException e) {
                    // Do not want to throw exception
                    //If the image is not found it just skip the image but other details will be come as response
                }
            }
        }
        return feeDepositDetailsList;
    }

    @Override
    public List<FeeDepositDetails> getStudentFeeSegregation(int schoolId, int sessionId, int classId, int sectionId, int studentId, String schoolCode) throws Exception {
        return feeDepositDetailsDao.getStudentFeeSegregation(schoolId, sessionId, classId, sectionId, studentId, schoolCode);
    }

    @Override
    public FeeDepositDetails getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(int classId, int sectionId, int sessionId, int studentId, String schoolCode) throws Exception {
        return feeDepositDetailsDao.getStudentFeeDetailsBasedOnClassSectionSessionAndStudent(classId, sectionId, sessionId, studentId, schoolCode);
    }

    @Override
    public List<FeeDepositDetails> getFeeDepositDetails(int fdId, int schoolId, String schoolCode) throws Exception {
        // return feeDepositDetailsDao.findFeeDepositDetailsById(fdId,schoolId,schoolCode);
        List<FeeDepositDetails> feeDeposits = feeDepositDetailsDao.findFeeDepositDetailsById(fdId, schoolId, schoolCode);

        // 2. Fetch school details and image
        SchoolDetails schoolDetails = schoolDao.getSchoolDetailsById(schoolId, schoolCode); // Assume this method exists
        try {
            SchoolDetails imageDetails = schoolDao.getImage(schoolCode, schoolId);
            schoolDetails.setSchoolImageString(imageDetails.getSchoolImageString());
        } catch (IOException e) {
            schoolDetails.setSchoolImageString(null); // Handle missing image gracefully
        }

        // 3. Attach school data to each fee deposit entry
        for (FeeDepositDetails deposit : feeDeposits) {
            deposit.setSchoolName(schoolDetails.getSchoolName());
            deposit.setSchoolAddress(schoolDetails.getSchoolAddress());
            deposit.setSchoolImage(schoolDetails.getSchoolImageString());
            // Add other school fields as needed
        }

        return feeDeposits;
    }

    @Override
    public List<FeeDepositDetails> getFeeDeposits(String schoolCode, int schoolId, int sessionId, Integer studentId, String studentName) throws Exception {
        try {
            return feeDepositDetailsDao.findFeeDeposits(
                    studentId,
                    studentName,
                    schoolId,
                    sessionId,
                    schoolCode
            );
        } catch (Exception e) {
            throw new ServiceException("Error fetching fee deposits", e);
        }
    }

/*    public Map<String, Object> getMonthlyDueDetailsForScreen(int schoolId, int sessionId, int studentId, String schoolCode) throws Exception {
        List<FeeDepositDetails> feeList = feeDepositDetailsDao.getAcademicFeesScreen(schoolId, sessionId, studentId, schoolCode);

        // Outer response map
        Map<String, Object> monthWiseResponse = new LinkedHashMap<>();

        // Grouping by month-year
        Map<String, List<FeeDepositDetails>> grouped = new LinkedHashMap<>();
        for (FeeDepositDetails fee : feeList) {
            if (fee.getDueDate() == null) continue;

            LocalDate localDueDate = new java.sql.Date(fee.getDueDate().getTime()).toLocalDate();
            String monthYear = localDueDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + localDueDate.getYear();

            grouped.computeIfAbsent(monthYear, k -> new ArrayList<>()).add(fee);
        }

        // Build response month by month
        for (Map.Entry<String, List<FeeDepositDetails>> entry : grouped.entrySet()) {
            String monthYear = entry.getKey();
            List<FeeDepositDetails> monthFees = entry.getValue();

            double totalDue = monthFees.stream()
                    .mapToDouble(FeeDepositDetails::getAmountDue)
                    .sum();

            // Earliest due date of the month
            LocalDate earliestDueDate = monthFees.stream()
                    .map(f -> new java.sql.Date(f.getDueDate().getTime()).toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(null);

            // If multiple statuses exist, pick rule: Paid > Partial > Pending
            String overallStatus = "Pending";
            if (monthFees.stream().allMatch(f -> "Paid".equalsIgnoreCase(f.getStatus()))) {
                overallStatus = "Paid";
            } else if (monthFees.stream().anyMatch(f -> "Paid".equalsIgnoreCase(f.getStatus()))) {
                overallStatus = "Partially Paid";
            }

            // Build details list
            List<Map<String, Object>> detailsList = new ArrayList<>();
            for (FeeDepositDetails fee : monthFees) {
                Map<String, Object> feeDetails = new LinkedHashMap<>();
                feeDetails.put("feeType", fee.getFeeType());
                feeDetails.put("dueDate", new java.sql.Date(fee.getDueDate().getTime()).toLocalDate().toString());
                feeDetails.put("amountDue", fee.getAmountDue());
                feeDetails.put("status", fee.getStatus());
                detailsList.add(feeDetails);
            }

            // Build month object
            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("totalDue", totalDue);
            monthData.put("dueDate", earliestDueDate != null ? earliestDueDate.toString() : null);
            monthData.put("status", overallStatus);
            monthData.put("details", detailsList);

            monthWiseResponse.put(monthYear, monthData);
        }
        System.out.println(monthWiseResponse);
        return monthWiseResponse;

    }*/

    public Map<String, Object> getMonthlyDueDetailsForScreen(int schoolId, int sessionId, int studentId, String schoolCode) throws Exception {
        List<FeeDepositDetails> feeList = feeDepositDetailsDao.getAcademicFeesScreen(schoolId, sessionId, studentId, schoolCode);

        Map<String, Object> finalResponse = new LinkedHashMap<>();

        if (!feeList.isEmpty()) {
            FeeDepositDetails first = feeList.get(0);

            // ✅ Put student/class/section info only once at top
            finalResponse.put("studentId", first.getStudentId());
            finalResponse.put("studentName", first.getStudentName());
            finalResponse.put("classId", first.getClassId());
            finalResponse.put("className", first.getClassName());
            finalResponse.put("sectionId", first.getSectionId());
            finalResponse.put("sectionName", first.getSectionName());
        }

        // Group by month
        Map<String, List<FeeDepositDetails>> grouped = new LinkedHashMap<>();
        for (FeeDepositDetails fee : feeList) {
            if (fee.getDueDate() == null) continue;

            LocalDate localDueDate = new java.sql.Date(fee.getDueDate().getTime()).toLocalDate();
            String monthYear = localDueDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + localDueDate.getYear();

            grouped.computeIfAbsent(monthYear, k -> new ArrayList<>()).add(fee);
        }

        Map<String, Object> monthWiseResponse = new LinkedHashMap<>();

        for (Map.Entry<String, List<FeeDepositDetails>> entry : grouped.entrySet()) {
            String monthYear = entry.getKey();
            List<FeeDepositDetails> monthFees = entry.getValue();

            double totalDue = monthFees.stream()
                    .mapToDouble(FeeDepositDetails::getAmountDue)
                    .sum();

            LocalDate earliestDueDate = monthFees.stream()
                    .map(f -> new java.sql.Date(f.getDueDate().getTime()).toLocalDate())
                    .min(LocalDate::compareTo)
                    .orElse(null);

            String overallStatus = "Pending";
            if (monthFees.stream().allMatch(f -> "Paid".equalsIgnoreCase(f.getStatus()))) {
                overallStatus = "Paid";
            } else if (monthFees.stream().anyMatch(f -> "Paid".equalsIgnoreCase(f.getStatus()))) {
                overallStatus = "Partially Paid";
            }

            // Build details list
            List<Map<String, Object>> detailsList = new ArrayList<>();
            for (FeeDepositDetails fee : monthFees) {
                Map<String, Object> feeDetails = new LinkedHashMap<>();
                feeDetails.put("feeType", fee.getFeeType());
                feeDetails.put("faId", fee.getFaId());
                feeDetails.put("fddtId", fee.getFddtId());
                feeDetails.put("dcId", fee.getDcId());
                feeDetails.put("dueDate", new java.sql.Date(fee.getDueDate().getTime()).toLocalDate().toString());
                feeDetails.put("discountAmount", fee.getDiscountAmount());
                feeDetails.put("amountDue", fee.getAmountDue());
                feeDetails.put("status", fee.getStatus());
                detailsList.add(feeDetails);
            }

            // Month data
            Map<String, Object> monthData = new LinkedHashMap<>();
            monthData.put("totalDue", totalDue);
            monthData.put("dueDate", earliestDueDate != null ? earliestDueDate.toString() : null);
            monthData.put("status", overallStatus);
            monthData.put("details", detailsList);

            monthWiseResponse.put(monthYear, monthData);
        }

        // ✅ Put months at last
        finalResponse.put("months", monthWiseResponse);

        return finalResponse;
    }
}




