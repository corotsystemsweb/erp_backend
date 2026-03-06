package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class StudentIdCardDetails {
    //student personal details
    private int id;
    private int schoolId;
    private String uuId;
    private String apaarId;
    private String firstName;
    private String lastName;
    private String bloodGroup;
    private String gender;
    private String height;
    private String weight;
    private String aadharNumber;
    private String phoneNumber;
    private String emergencyPhoneNumber;
    private String whatsAppNumber;
    private String emailAddress;
    private String fatherName;
    private String fatherOccupation;
    private String motherName;
    private String motherOccupation;
    private Date dob;
    private String dobCirtificateNo;
    private String incomeAppNo;
    private String casteAppNo;
    private String domicileAppNo;
    private String govtStudentIdOnPortal;
    private String govtFamilyIdOnPortal;
    private String bankName;
    private String branchName;
    private String ifscCode;
    private String accountNumber;
    private String panNo;
    private String religion;
    private String nationality;
    private String category;
    private String caste;
    private String currentAddress;
    private String currentCity;
    private String currentState;
    private int currentZipCode;
    private String permanentAddress;
    private String permanentCity;
    private String permanentState;
    private int permanentZipCode;
    private String studentCountry;
    private String studentPhoto;
    private String studentImageUrl;
    //student academic details
    private String penNo;
    private String admissionNo;
    private Date admissionDate;
    private int studentId;
    private String registrationNumber;
    private String rollNumber;
    private String academicSession;
    private int sessionId;
    private int studentClassId;
    private int studentSectionId;
    private String stream;
    private String educationMedium;
    private String referredBy;
    private boolean isRteStudent;
    private String rteApplicationNo;
    private String enrolledClass;
    private String enrolledSession;
    private String enrolledYear;
    private String transferCirtiNo;
    private Date dateOfIssue;
    private String scholarshipId;
    private String scholarshipPassword;
    private String lstSchoolName;
    private String lstSchoolAddress;
    private String lstAttendedClass;
    private String lstSclAffTo;
    private String lstSession;
    private boolean isDropOut;
    private Date dropOutDate;
    private String dropOutReason;
    private String studentAdmissionType;
    private String currentStatus;
    private String currentStatusComment;
    private int updatedBy;
    private Date updatedDate;
    private Date createDate;
    private Date validityStartDate;
    private Date validityEndDate;
    private String sessionStatus;
    private String sessionStatusComment;
    private List<PreviousQualificationDetails> previousQualificationDetails;
    //for class and section
    private int classId;
    private String className;
    private int sectionId;
    private String sectionName;
    //for displaying student photo
    private String studentImage;
    private String schoolName;
    private String schoolAddress;
    private String schoolCity;
    private String schoolState;
    private String schoolCountry;
    private int schoolZipCode;
    private String schoolEmailAddress;
    private String schoolPhoneNumber;
    //for displaying parent details
    private List<ParentDetails> parentDetails;
    private List<Integer> parentId;
}
