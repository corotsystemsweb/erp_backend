package com.sms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class TransportFeeDepositDetails {
    private int tfddId;
    private int tfdId;
    private int studentTransportId;
    private Date dueMonth;
    private double feeAmount;
    private double discountAmount;
    private double penaltyAmount;
    private double amountPaid;
    private double balance;
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime systemDateTime;
    private int schoolId;
    private int sessionId;
    private int classId;
    private int sectionId;
    private int studentId;
    private int routeId;
    private int paymentMode;
    private double totalAmountPaid;
    private int paymentReceivedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;
    private String transactionId;
    private String paymentDescription;
}
