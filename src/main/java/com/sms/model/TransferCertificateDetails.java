package com.sms.model;

import lombok.Data;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class TransferCertificateDetails {
    private Long tcId;
    private Integer schoolId;
    private String schoolCode;
    private String schoolCategory;
    private Integer sessionId;
    private String academicSession;
    private Timestamp issuedDate;
    private Integer issuedBy;
    private Long studentId;
    private String admissionNo;
    private String studentName;
    private Date dob;
    private String dobInWords;
    private String proofOfDob;
    private String penNo;
    private String apaarId;
    private String fatherName;
    private String motherName;
    private String guardianName;
    private String isStudentFailed;
    private String subjectsOffered;
    private String isPromotedToNextClass;
    private Integer noOfMeetings;
    private Integer totalPresent;
    private String classAtLeaving;
    private String sectionAtLeaving;
    private String lastAnnualExamResult;
    private String lastStudiedClass;
    private String category;
    private String feeDueStatus;
    private String  extracurricular;
    private String gamesActivities;
    private String generalConduct;
    private String reasonForLeaving;
    private Date dateOfIssue;
    private Date dateStruckOff;
    private Integer totalPresence;
    private String subjectsStudied;
    private String remarks;
    private String tcData;
    private String srNumber;
    private String lastExamPassed;
    private StudentDetails studentDetails;
    private Boolean isEligible;
    private Date dateOfFirstAdmission;
    private String lastClassStudied;
    private Integer totalWorkingDays;
    private String feeConcession;
    private Date dateOfApplication;
}
