package com.sms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class FeeDepositDetails {
    private int fddId;
    private int fdId;
    private int faId;
    private  int fddtId;
    private double amountPaid;
    private int dcId;
    private double discountAmount;
    private int penaltyId;
    private double penaltyAmount;
    private double balance;
    private double totalAmountAfterDiscount;
    private int approvedBy;
    private String status;
    private int schoolId;
    private String schoolBuilding;
    public String schoolAddress;
    private String schoolEmailAddress;
    private String schoolPhoneNumber;
    private String schoolCity;
    private String schoolState;
    private String schoolCountry;
    private String schoolZipCode;
    private String schoolName;
    private int sessionId;
    private String sessionName;
    private int classId;
    private String className;
    private int sectionId;
    private String sectionName;
    private int studentId;
    private String studentName;
    private String admissionNumber;
    private String rollNumber;
    private String gender;
    private String registrationNumber;
    private int paymentMode;
    private int totalAmountPaid;
    private int paymentReceivedBy;
    private Date systemDateTime;
    private Date dueDate;
    private String paymentDescription;
    private String transactionId;
    private int maxFdIdAsTransactionId;
    private String comment;
    private int pmId;
    private String paymentType;
    private int feeId;
    private String feeType;
    private int frequencyId;
    private String frequencyType;
    private double totalAmountPaidByStudent;
    private String year;
    private double totalYearlyAmountDeposit;
    private List<String> totalAmountPaidList;
    private String fddStatus;
    private List<String> feeDetails;
    private double totalFeeAssigned;
    private double totalDiscount;
    private double grossStudentFee;
    private double totalPenalty;
    private String penaltyTypes;
    private double totalPaid;
    private double totalDue;
    private List<Integer> parentId;
    private String parentName;
    private String parentMobileNumber;
    private List<ParentDetails> parentDetails;
    private double feeAmount;
    private double amountAfterDiscount;
    private double amountDue;
    private String lastPaymentMode;
    private double totalPayableAmount;
    private String studentImage;
    private List<FeeDetail> feeDetail;
    private String formattedPaymentDate;
    private String schoolImage;
    private Double additionalDiscount;
    private  String additionalDiscountReason;
    @JsonProperty("paymentDate")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date paymentDate;
}
