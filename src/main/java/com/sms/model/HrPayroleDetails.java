package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class HrPayroleDetails {
    private int ssId;
    private int schoolId;
    private int sessionId;
    private int staffId;
    private int departmentId;
    private int designationId;
    private String salaryAmount;
    private String designation;
    private String department;
    private String staffName;
}
