package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class StudentExcelDetails {
    private String apaarId;
    private int studentId;
    private String uuid;
    private String session;
    private String studentFirstName;
    private String studentLastName;
    private String studentClass;
    private String studentSection;
    private int rollNo;
    private String medium;
    private Date dob;
    private String religion;
    private String nationality;
    private String gender;
    private String mobileNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String country;
    private String pincode;
    private String type;
    private boolean isRteStudent;
    private String studentMotherName;
    private String studentFatherName;
    private String guardianName;
    private Date admissionDate;
    private String enrolledSession;
    private String enrolledYear;
    private String enrolledClass;
    private String penNo;
    private String studentType;
    private String admissionNo;
    private String registrationNo;
    private String enrollmentNo;
    private String stream;
    private String whatsapp;
    private String alternateNumber;
    private String bloodGroup;
    private String aadharNo;
    private String caste;
    private String category;
    private String rteApplicationNo;
    private String attendedSchool;
    private String attendedClass;
    private String schoolAffiliated;
    private String lastSession;
    private String motherQualification;
    private String fatherQualification;
    private String guardianQualification;
    private String motherOccupation;
    private String fatherOccupation;
    private String guardianOccupation;
    private String motherResidentialAddress;
    private String fatherResidentialAddress;
    private String guardianResidentialAddress;
    private String motherIncome;
    private String fatherIncome;
    private String guardianIncome;
    private String motherEmail;
    private String fatherEmail;
    private String guardianEmail;
    private String motherMobile;
    private String fatherMobile;
    private String guardianMobile;
    private String tcNo;
    private String tcDate;
    private String scholarshipId;
    private String scholarshipPassword;
    private String domicileApplicationNo;
    private String incomeApplicationNo;
    private String casteApplicationNo;
    private String dobApplicationNo;
    private String motherAadharNo;
    private String fatherAadharNo;
    private String guardianAadharNo;
    private String height;
    private String weight;
    private String bankName;
    private String branchName;
    private String bankAccountNo;
    private String bankIfsc;
    private String panNo;
    private String reference;
    private String govtStudentId;
    private String govtFamilyId;
    private boolean dropout;
    private String dropoutReason;
    private Date dropoutDate;
    private String previousQualification;
    private String previousPassYear;
    private String previousRollNo;
    private String previousObtMarks;
    private String previousPercentage;
    private String previousSubjects;
    private String previousSchoolName;
}
