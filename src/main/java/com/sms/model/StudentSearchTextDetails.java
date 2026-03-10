package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class StudentSearchTextDetails {
    private int studentId;
    private String firstName;
    private String lastName;
    private String className;
    private String admissionNumber;
    private String gender;
    private String studentType;
    private String sectionName;
    private String phoneNumber;
    private String registrationNumber;
    private String fatherName;
    private List<ParentDetails> parentDetails;
    private String studentImage;
    ///
    private int schoolId;
    private String schoolName;
    private String schoolBuilding;
    private String schoolAddress;
    private String schoolEmailAddress;
    private String schoolPhoneNumber;
    private String schoolCity;
    private String schoolState;
    private String schoolCountry;
    private String schoolZipCode;
    private String studentName;
    private int classId;
    private int sectionId;
    private int sessionId;
    private String sessionName;
    private List<String> feeDetails;
    private double totalFeeAssigned;
    private double totalDiscount;
    private double grossStudentFee;
    private double totalPenalty;
    private double totalPaid;
    private double totalDue;

}
