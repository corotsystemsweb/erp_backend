package com.sms.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TransportFeeDepositRequest {
    private int schoolId;
    private int sessionId;
    private int classId;
    private int sectionId;
    private int studentId;
    private int routeId;
    private int paymentMode;
    private int paymentReceivedBy;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentDate;
    private String transactionId;
    private String paymentDescription;
    private String status;
    private List<TransportFeeDepositDetails> transportFeeDepositDetails;
}
