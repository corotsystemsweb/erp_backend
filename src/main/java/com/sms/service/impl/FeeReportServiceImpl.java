package com.sms.service.impl;

import com.sms.dao.FeeReportDAO;
import com.sms.dao.SchoolDao;
import com.sms.model.*;
import com.sms.service.FeeReportService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FeeReportServiceImpl implements FeeReportService {
    private final FeeReportDAO feeReportDAO;

    private SchoolDao schoolDao;

    public FeeReportServiceImpl(FeeReportDAO feeReportDAO, SchoolDao schoolDao) {
        this.feeReportDAO = feeReportDAO;
        this.schoolDao = schoolDao;
    }

    @Override
    public List<FeeSummary> getFeeSummary(String reportType, LocalDate startDate, LocalDate endDate,String schoolCode) {
        return feeReportDAO.getFeeSummary(reportType,startDate,endDate,schoolCode);
    }

    @Override
    public List<Map<String, Object>> getPendingFees(String level, Long classId, Long sectionId,
                                                    Long studentId, Long sessionId, String schoolCode) {
        //schoolService.validateSchool(schoolCode);

        return switch (level.toUpperCase()) {
            case "CLASS" -> {
                validateClassParams(classId, sessionId);
                yield feeReportDAO.getClassPendingFees(classId, sessionId, schoolCode);
            }
            case "SECTION" -> {
                validateSectionParams(classId, sectionId, sessionId);
                yield feeReportDAO.getSectionPendingFees(classId, sectionId, sessionId, schoolCode);
            }
            case "STUDENT" -> {
                validateStudentParams(studentId, sessionId);
                yield feeReportDAO.getStudentPendingFees(classId,sectionId,studentId, sessionId, schoolCode);
            }
            default -> throw new IllegalArgumentException("Invalid report level: " + level);
        };
    }


   /* @Override
    public List<Map<String, Object>> getDemandSlip(Long classId, Long sectionId,
                                                   Long studentId, Long sessionId,
                                                   String schoolCode) {
        validateParameters(classId, sectionId, studentId, sessionId);
        return feeReportDAO.getDemandSlips(classId, sectionId, studentId,
                sessionId, schoolCode);
    }*/

    @Override
    public List<PendingFee> getDemandSlip(Long classId, Long sectionId,
                                          Long studentId, Long sessionId,
                                          String schoolCode, LocalDate cutoffDate) {
        validateParameters(classId, sectionId, studentId, sessionId);
        SchoolDetails schoolDetails = getSchoolDetailsWithImage(schoolCode);

        List<Map<String, Object>> rawData = feeReportDAO.getDemandSlips(
                classId, sectionId, studentId, sessionId, schoolCode,cutoffDate
        );

        return processRawData(rawData,schoolDetails);
    }

    @Override
    public List<FeeSummary> getFeeSummaryByClass(String schoolCode) throws Exception {
        return feeReportDAO.getFeeSummaryByClass(schoolCode);
    }

    @Override
    public List<MonthWiseFeeDueDetails> getMonthlyDueDetails(int sessionId, int classId, Integer sectionId, Integer studentId, String monthFilter, String schoolCode) {
        return feeReportDAO.fetchMonthlyDueDetails(sessionId,classId,sectionId,studentId,monthFilter,schoolCode);
    }


    private SchoolDetails getSchoolDetailsWithImage(String schoolCode) {
        try {
            List<SchoolDetails> schools = schoolDao.getAllSchoolDetails(schoolCode);
            if (!schools.isEmpty()) {
                SchoolDetails school = schools.get(0);

                // Add school image
                SchoolDetails imageDetails = schoolDao.getImage(schoolCode, school.getSchoolId());
                school.setSchoolImageString(imageDetails.getSchoolImageString());

                return school;
            }
        } catch (Exception e) {
            // Handle exception or log error
            e.printStackTrace();
        }
        return new SchoolDetails(); // Return empty object if no details found
    }

    private List<PendingFee> processRawData(List<Map<String, Object>> rawData,SchoolDetails schoolDetails) {
        Map<Long, PendingFee> resultMap = new LinkedHashMap<>();

        for (Map<String, Object> row : rawData) {
            Long studentId = ((Number) row.get("student_id")).longValue();

            PendingFee pendingFee = resultMap.computeIfAbsent(studentId, k ->
                    createBasePendingFee(row, studentId)
            );
            pendingFee.setSchoolDetails(schoolDetails);


            DemandSlip demandSlip = createDemandSlip(row);
            pendingFee.getFeeDetails().add(demandSlip);

            // Update total pending amount
            pendingFee.setPendingAmount(
                    pendingFee.getPendingAmount().add(demandSlip.getPendingAmount())
            );
        }

        return new ArrayList<>(resultMap.values());
    }

    private PendingFee createBasePendingFee(Map<String, Object> row, Long studentId) {
        PendingFee pendingFee = new PendingFee();
        pendingFee.setStudentId(studentId);
        pendingFee.setStudentName((String) row.get("student_name"));
        pendingFee.setClassName((String) row.get("class_name"));
        pendingFee.setSectionName((String) row.get("section_name"));
        pendingFee.setAcademicSession((String) row.get("academic_session"));
        pendingFee.setPendingAmount(BigDecimal.ZERO);
        return pendingFee;
    }
    private DemandSlip createDemandSlip(Map<String, Object> row) {
        DemandSlip slip = new DemandSlip();
        slip.setDueDate(((java.sql.Date) row.get("due_date")).toLocalDate());
        slip.setFeeType((String) row.get("fee_type"));
        slip.setTotalAmount(new BigDecimal(row.get("fee_amount").toString()));
        slip.setDiscountAmount(new BigDecimal(row.get("discount_amount").toString()));
        slip.setPaidAmount(new BigDecimal(row.get("paid_amount").toString()));
        slip.setPendingAmount(new BigDecimal(row.get("pending_amount").toString()));
        return slip;
    }

    private void validateParameters(Long classId, Long sectionId, Long studentId,
                                    Long sessionId) {
        if (sessionId == null) {
            throw new IllegalArgumentException("Session ID is mandatory");
        }

        if (studentId == null && classId == null && sectionId == null) {
            throw new IllegalArgumentException("At least one filter parameter (class, section, or student) is required");
        }
    }

    private void validateClassParams(Long classId, Long sessionId) {
        if (classId == null || sessionId == null) {
            throw new IllegalArgumentException("Class ID and Session ID are required");
        }
    }

    private void validateSectionParams(Long classId, Long sectionId, Long sessionId) {
        if (classId == null || sectionId == null || sessionId == null) {
            throw new IllegalArgumentException("Class ID, Section ID and Session ID are required");
        }
    }

    private void validateStudentParams(Long studentId, Long sessionId) {
        if (studentId == null || sessionId == null) {
            throw new IllegalArgumentException("Student ID and Session ID are required");
        }
    }
}
