package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentEnquiryFormDetails {
    private Integer id;

    // ===== BASIC DETAILS =====
    private String srNo;
    private String admissionDate; // or LocalDate
    private String admissionNo;
    private String classSought;
    private String session;
    private String pen;
    private String apaarId;

    // ===== PERSONAL DETAILS =====
    private String studentName;
    private String gender;
    private String dob; // or LocalDate
    private String dobInWords;

    // ===== MOTHER DETAILS =====
    private String motherName;
    private String motherPhone;
    private String motherQualification;
    private String motherEmail;
    private String motherOccupation;
    private String motherLocalAddress;
    private String motherResidentialAddress;
    private BigDecimal motherAnnualIncome;

    // ===== FATHER DETAILS =====
    private String fatherName;
    private String fatherPhone;
    private String fatherQualification;
    private String fatherEmail;
    private String fatherOccupation;
    private String fatherLocalAddress;
    private String fatherResidentialAddress;
    private BigDecimal fatherAnnualIncome;

    // ===== GUARDIAN DETAILS =====
    private String guardianName;
    private String guardianPhone;
    private String guardianQualification;
    private String guardianEmail;
    private String guardianOccupation;
    private String guardianLocalAddress;
    private String guardianResidentialAddress;
    private BigDecimal guardianAnnualIncome;

    // ===== STATUS =====
    private Boolean isSingleGirlChild;
    private Boolean isSpeciallyAbled;

    // ===== CATEGORY =====
    private String category;
    private String religion;

    // ===== AADHAR =====
    private String aadharNumber;

    // ===== LAST SCHOOL =====
    private String lastSchoolName;
    private String lastSchoolAddress;
    private String lastClassAttended;
    private String lastSchoolBoard;

    // ===== RESULT =====
    private String lastClassResult;

    // ===== TC =====
    private String transferCertificateNumber;
    private String tcDateOfIssue; // or LocalDate

    // ===== JSON FIELDS =====
    private List<StudentEnquirySiblings> siblings;
    private List<StudentEnquirySubjects> subjects;

    // ===== DECLARATION =====
    private String declarationText;
    private String declarationDate; // or LocalDate
    private String place;
    private String parentSignature;
    private String relationshipWithCandidate;
    private String principalSignature;

    // ===== OFFICE =====
    private String registerPageNo;
    private String registerEntryDate; // or LocalDate

    // ===== SYSTEM =====
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String studentImage;
}
