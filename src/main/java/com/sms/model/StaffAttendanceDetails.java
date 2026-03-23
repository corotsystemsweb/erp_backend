package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StaffAttendanceDetails {
    private int saId;
    private int schoolId;
    private int sessionId;
    private String academicSession;
    private Integer staffId;
    private String staffName;
    private String staffType;
    private String phoneNumber;
    private Integer designationId;
    private String designation;
    private Integer departmentId;
    private String departmentName;
    private Date attendanceDate;
    private Date dateFrom;
    private Date dateTo;
    private String attendanceStatus;
    private Time checkInTime;
    private Time checkOutTime;
    private double totalDays;
    private double totalPresent;
    private double totalAbsent;
    private String dailyDetails;
    private double attendancePercentage;
}
