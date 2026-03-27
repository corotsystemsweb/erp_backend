package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignFeeToStudentsDetails {
    private int assignmentId;
    private int classId;
    private String className;
    private List<Integer> sectionIds;
    private List<String> sectionNames;
    private String feeType;
    private double amount;
    private String frequency;
    private Date startDate;
    private String duration;
    private int studentsCount;
    private String discount;
    private String status;
}
