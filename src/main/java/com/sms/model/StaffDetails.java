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
public class StaffDetails {
    private int staffId;
    private String firstName;
    private String lastName;
    private String staffName;
    private String registrationNumber;
    private Date joiningDate;
    private int departmentId;
    private String department;
    private int designationId;
    private String designation;
    private String  fatherName;
    private String bloodGroup;
    private String gender;
    private String aadharNumber;
    private String highestQualification;
    private String pfAccountNo;
    private String experienceDetails;
    private String phoneNumber;
    private String emailAddress;
    private Date dob;
    private String religion;
    private String emergencyPhoneNumber;
    private String emergencyContactName;
    private String currentAddress;
    private int currentZipCode;
    private String currentCity;
    private String currentState;
    private String permanentAddress;
    private int permanentZipCode;
    private String permanentCity;
    private String permanentState;
    private String staffCountry;
    private String currentStatus;
    private String currentStatusComment;
    private String staffPhoto;
    private String staffImage;
    private boolean deleted;
    private String salaryAmount;
    private int schoolId;
    private String schoolCode;
    private String schoolName;
    private String schoolBuilding;
    private String schoolAddress;
    private String schoolEmailAddress;
    private String schoolCity;
    private String schoolState;
    private String schoolCountry;
    private String schoolPhoneNumber;
    private String schoolBankDetails;
    private String schoolBranchName;
    private String schoolAccountNumber;
    private String schoolIfscCode;
    private String schoolAlternatePhoneNumber;
    private String schoolZipCode;
    private int sessionId;
    private String employmentType;
    private String experience;
    private BankDetails bankDetails;
    private HrPayroleDetails hrPayroleDetails;
}
