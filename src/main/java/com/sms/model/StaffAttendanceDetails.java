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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffAttendanceDetails {
    private int saId;
    private int staffId;
    private String staffName;
    private String staffType;
    private String phoneNumber;
    private int designationId;
    private String designation;
    private Date attendanceDate;
    private Date dateFrom;
    private Date dateTo;
    private String attendanceStatus;
    private double totalDays;
    private double totalPresent;
    private double totalAbsent;
    private String dailyDetails;
    private double attendancePercentage;
}
