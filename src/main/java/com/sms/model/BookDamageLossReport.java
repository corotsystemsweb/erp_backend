package com.sms.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDamageLossReport {
    private int reportId;
    private int schoolId;
    private int sessionId;
    private int memberId;
    private int bookId;
    private String reportType;
    private Double fineAmount;
    private Date reportDate;
    private String reason;
    private Integer updatedBy;
    private Timestamp updateDateTime;
    private boolean deleted;


}
