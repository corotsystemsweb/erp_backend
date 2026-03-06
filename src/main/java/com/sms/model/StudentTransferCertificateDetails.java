package com.sms.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentTransferCertificateDetails {
    private Long tcId;
    private Long studentId;
    private Integer schoolId;
    private Integer sessionId;
    private Timestamp issuedDate;
    private Integer issuedBy;
    private String admissionNo;
    private String studentName;
    private Date dob;
    private String dobInWords;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private String classAtLeaving;
    private String sectionAtLeaving;
    private String lastExamPassed;
    private String subjectsStudied;
    private String reasonForLeaving;
    private String feeDuesStatus;
    private String remarks;
    private String tcData;
    private StudentDetails studentDetails;
    private Boolean isEligible;
    private String proofOfDob;
    private String category;
    private Date dateOfFirstAdmission;
    private String lastClassStudied;
    private Integer totalWorkingDays;
    private Integer totalPresence;
    private String feeConcession;
    private String  extracurricular;
    private String schoolCategory;
    private String gamesActivities;
    private Date dateOfApplication;
    private Date dateStruckOff;
    private Date dateOfIssue;
    private SchoolDetails schoolDetails;
    private String schoolImageBase64;
}
