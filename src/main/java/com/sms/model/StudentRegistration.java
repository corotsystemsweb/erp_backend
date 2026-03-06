package com.sms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;



@Getter
@Setter
@Entity
@Table(name = "student_registration")
public class StudentRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // Basic student info
    private int stdRegistrationId;
    private Integer schoolId;
    private String firstName;
    private String lastName;
    private Date dob;
    private String bloodGroup;
    private String religion;
    private String aadhaarNumber;
    private String studentPenNo;

    private String contactNo;
    private String alternateNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String pinCode;
    private String fatherName;
    private String motherName;
    private int sessionId;
    private String qualification;
    private String enrolledClass;
    private String lastInstituteName;
    private String transferCertificateNo;
    private String referenceName;
    private String referenceType;

    private Date registrationDate;
    private String enquiryStatus;
    private String status;
    private String comment;
}
