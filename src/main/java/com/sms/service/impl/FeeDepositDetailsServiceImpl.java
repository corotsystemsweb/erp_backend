package com.sms.service.impl;
import com.sms.model.FeeDetail;
import java.util.Collections;
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
//        double totalAmount = feeDepositRequest.getFeeDetails().stream()
//                .mapToDouble(FeeDepositDetails::getAmountPaid)
//                .sum();
        double totalAmount = feeDepositRequest.getFeeDetails().stream()
                .mapToDouble(FeeDepositDetails::getAmountPaid)
                .sum();

// ✅ If transport-only payment (no academic fees), skip academic fee deposit creation
        boolean isTransportOnly = feeDepositRequest.getFeeDetails() == null
                || feeDepositRequest.getFeeDetails().isEmpty();

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
//        FeeDepositDetails insertedFeeDeposit = feeDepositDao.addFeeDeposit(feeDeposit, schoolCode);
//
//        // Set the fd_id for each feeDepositDetails entry
//        for (FeeDepositDetails detail : feeDepositRequest.getFeeDetails()) {
//            detail.setFdId(insertedFeeDeposit.getFdId());
//            detail.setApprovedBy(feeDepositRequest.getApprovedBy());
//            detail.setPaymentReceivedBy(feeDepositRequest.getPaymentReceivedBy());
//        }
//
//        // Insert into fee_deposit_details
//        List<FeeDepositDetails> addedDetails = feeDepositDetailsDao.addFeeDepositDetails(feeDepositRequest.getFeeDetails(), schoolCode);
//
//        response.setAcademicFeeDetails(addedDetails);

        if (!isTransportOnly) {
            FeeDepositDetails insertedFeeDeposit = feeDepositDao.addFeeDeposit(feeDeposit, schoolCode);

            for (FeeDepositDetails detail : feeDepositRequest.getFeeDetails()) {
                detail.setFdId(insertedFeeDeposit.getFdId());
                detail.setApprovedBy(feeDepositRequest.getApprovedBy());
                detail.setPaymentReceivedBy(feeDepositRequest.getPaymentReceivedBy());
            }

            List<FeeDepositDetails> addedDetails = feeDepositDetailsDao
                    .addFeeDepositDetails(feeDepositRequest.getFeeDetails(), schoolCode);
            response.setAcademicFeeDetails(addedDetails);
        } else {
            // ✅ Transport-only: set empty list so frontend knows
            response.setAcademicFeeDetails(Collections.emptyList());
        }

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
            // ✅ For transport-only: expose tfd_id so frontend can navigate to receipt
            response.setTfdId(transportFeeDepositDetails.getTfdId());
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
//        List<FeeDepositDetails> feeDeposits = feeDepositDetailsDao.findFeeDepositDetailsById(fdId, schoolId, schoolCode);

        List<FeeDepositDetails> feeDeposits = feeDepositDetailsDao.findFeeDepositDetailsById(fdId, schoolId, schoolCode);

// ✅ TRANSPORT-ONLY FALLBACK: if no academic fee found, check transport table
        if (feeDeposits == null || feeDeposits.isEmpty()) {
            try {
                // fdId is actually academic fee ID, but we need to check if it could be tfd_id
                // Try to find if this ID exists in transport_fee_deposit table
                String checkSql = "SELECT tfd_id FROM transport_fee_deposit WHERE tfd_id = ? AND school_id = ?";
                org.springframework.jdbc.core.JdbcTemplate jt =
                        com.sms.util.DatabaseUtil.getJdbctemplateForSchool(schoolCode);
                Integer tfdId = null;
                try {
                    tfdId = jt.queryForObject(checkSql, Integer.class, fdId, schoolId);
                } catch (Exception ex) {
                    // Not a transport ID
                } finally {
                    com.sms.util.DatabaseUtil.closeDataSource(jt);
                }

                if (tfdId != null) {
                    FeeDepositDetails transportReceipt = buildTransportOnlyReceipt(tfdId, schoolId, schoolCode);
                    if (transportReceipt != null) {
                        feeDeposits = new ArrayList<>();
                        feeDeposits.add(transportReceipt);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2. Fetch school details and image
        SchoolDetails schoolDetails = schoolDao.getSchoolDetailsById(schoolId, schoolCode); // Assume this method exists
        try {
            SchoolDetails imageDetails = schoolDao.getImage(schoolCode, schoolId);
            schoolDetails.setSchoolImageString(imageDetails.getSchoolImageString());
        } catch (IOException e) {
            schoolDetails.setSchoolImageString(null); // Handle missing image gracefully
        }

//        // 3. Attach school data to each fee deposit entry
//        for (FeeDepositDetails deposit : feeDeposits) {
//            deposit.setSchoolName(schoolDetails.getSchoolName());
//            deposit.setSchoolAddress(schoolDetails.getSchoolAddress());
//            deposit.setSchoolImage(schoolDetails.getSchoolImageString());
//            // Add other school fields as needed
//        }
//
//
//
//        return feeDeposits;


        // 3. Attach school data to each fee deposit entry
        for (FeeDepositDetails deposit : feeDeposits) {
            deposit.setSchoolName(schoolDetails.getSchoolName());
            deposit.setSchoolAddress(schoolDetails.getSchoolAddress());
            deposit.setSchoolImage(schoolDetails.getSchoolImageString());

            // ✅ Skip transport merge if this is a transport-only receipt
            // (buildTransportOnlyReceipt already set feeDetail correctly)
            boolean isTransportOnly = deposit.getFeeDetail() != null
                    && !deposit.getFeeDetail().isEmpty()
                    && deposit.getFeeDetail().stream()
                    .allMatch(f -> f.getFeeType() != null && "Transport".equals(f.getFeeType()));

            boolean transportAlreadyIncluded = deposit.getFeeDetail() != null
                    && deposit.getFeeDetail().stream()
                    .anyMatch(f -> "Transport Fee".equals(f.getFeeType()));

            if (!isTransportOnly && !transportAlreadyIncluded) {
                String transactionId = deposit.getTransactionId();
                if (transactionId != null && !transactionId.isEmpty()) {
                    try {
                        List<FeeDetail> transportFeeDetails =
                                transportFeeDepositDetailsDao.findTransportFeeDetailsByTransactionId(
                                        transactionId, schoolId, schoolCode
                                );

                        if (transportFeeDetails != null && !transportFeeDetails.isEmpty()) {
                            List<FeeDetail> allFeeDetails = new ArrayList<>(
                                    deposit.getFeeDetail() != null
                                            ? deposit.getFeeDetail()
                                            : Collections.emptyList()
                            );
                            allFeeDetails.addAll(transportFeeDetails);
                            deposit.setFeeDetail(allFeeDetails);

                            double transportTotal = transportFeeDetails.stream()
                                    .mapToDouble(FeeDetail::getAmountPaid)
                                    .sum();
                            deposit.setTotalPaid(deposit.getTotalPaid() + transportTotal);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
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



    private FeeDepositDetails buildTransportOnlyReceipt(int tfdId, int schoolId, String schoolCode) throws Exception {

        // 1. Fetch the transport_fee_deposit header row using tfd_id
        String sql = """
        SELECT tfd_id, student_id, session_id, class_id,
               section_id, transaction_id, payment_date,
               total_amount_paid, payment_mode
        FROM transport_fee_deposit
        WHERE tfd_id = ?
          AND school_id = ?
        """;
        org.springframework.jdbc.core.JdbcTemplate jt =
                com.sms.util.DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        FeeDepositDetails receipt;
        try {
            receipt = jt.queryForObject(sql, new Object[]{tfdId, schoolId},
                    (rs, rowNum) -> {
                        FeeDepositDetails d = new FeeDepositDetails();
                        d.setStudentId(rs.getInt("student_id"));
                        d.setSessionId(rs.getInt("session_id"));
                        d.setClassId(rs.getInt("class_id"));
                        d.setSectionId(rs.getInt("section_id"));
                        d.setTransactionId(rs.getString("transaction_id"));
                        d.setTotalPaid(rs.getDouble("total_amount_paid"));
                        d.setPaymentMode(rs.getInt("payment_mode"));
                        java.sql.Timestamp paymentDate = rs.getTimestamp("payment_date");
                        if (paymentDate != null) {
                            d.setFormattedPaymentDate(
                                    new java.text.SimpleDateFormat("dd MMM yyyy")
                                            .format(paymentDate)
                            );
                            d.setPaymentDate(paymentDate);
                        }
                        return d;

                    });

        } catch (Exception e) {
            return null; // tfdId not found in transport table either
        } finally {
            com.sms.util.DatabaseUtil.closeDataSource(jt);
        }

        if (receipt == null) return null;

// 2. Attach school info
        SchoolDetails schoolDetails = schoolDao.getSchoolDetailsById(schoolId, schoolCode);
        try {
            SchoolDetails imageDetails = schoolDao.getImage(schoolCode, schoolId);
            schoolDetails.setSchoolImageString(imageDetails.getSchoolImageString());
        } catch (Exception e) {
            schoolDetails.setSchoolImageString(null);
        }
        receipt.setSchoolName(schoolDetails.getSchoolName());
        receipt.setSchoolAddress(schoolDetails.getSchoolAddress());
        receipt.setSchoolImage(schoolDetails.getSchoolImageString());

// 3. ✅ Fetch student name, class, section, session using student_id
        // 3. ✅ Fetch student name, class, section, session using tfd_id directly
        org.springframework.jdbc.core.JdbcTemplate jt2 =
                com.sms.util.DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            String studentSql = """
    SELECT 
        spd.first_name || ' ' || spd.last_name AS student_name,
        sad.admission_no,
        spd.gender,
        c.class_name, 
        s.section_name,
        ses.academic_session as session_name
    FROM student_academic_details sad
    LEFT JOIN student_personal_details spd 
        ON spd.student_id = sad.student_id 
        AND spd.school_id = sad.school_id
    LEFT JOIN mst_class c 
        ON sad.student_class_id = c.class_id
    LEFT JOIN mst_section s 
        ON sad.student_section_id = s.section_id
    LEFT JOIN session ses 
        ON sad.session_id = ses.session_id
    WHERE sad.student_id = ?
      AND sad.session_id = ?
      AND sad.school_id = ?
    """;





            jt2.queryForObject(studentSql,
                    new Object[]{receipt.getStudentId(), receipt.getSessionId(), schoolId},
                    (rs, rowNum) -> {
                        receipt.setStudentName(rs.getString("student_name"));
                        receipt.setAdmissionNumber(rs.getString("admission_no"));
                        receipt.setGender(rs.getString("gender"));
                        receipt.setClassName(rs.getString("class_name"));
                        receipt.setSectionName(rs.getString("section_name"));
                        receipt.setSessionName(rs.getString("session_name"));
                        return null;
                    });



        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            com.sms.util.DatabaseUtil.closeDataSource(jt2);
        }

// 4. Fetch transport fee detail rows
        List<FeeDetail> transportFeeDetails =
                transportFeeDepositDetailsDao.findTransportFeeDetailsByTransactionId(
                        receipt.getTransactionId(), schoolId, schoolCode
                );
        receipt.setFeeDetail(transportFeeDetails != null ? transportFeeDetails : new ArrayList<>());

        return receipt;
    }
}




