package com.sms.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PreviousQualificationDetails {
    private String qualification;
    private String passingYear;
    private String rollNumber;
    private double obtainedMarks;
    private double percentage;
    private List<String> subjects;
    private String sclClgName;
}
