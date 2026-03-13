package com.sms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;
@Data
public class FeeDepositRequest {
    private int schoolId;
    private int sessionId;
    private int classId;
    private int sectionId;
    private int studentId;
    private int paymentMode;
    private int paymentReceivedBy;
    private int approvedBy;
    private Date paymentDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime transportPaymentDate;
    private String paymentDescription;
    private String transactionId;
    private String comment;
    private Double additionalDiscount;
    private  String additionalDiscountReason;
    private int routeId;
    private String status;
    private List<FeeDepositDetails> feeDetails;
    private List<TransportFeeDepositDetails> transportFeeDepositDetails;
}
