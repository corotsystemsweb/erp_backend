package com.sms.model;
import lombok.Data;

import java.util.List;

@Data
public class ExamResultDetails {
    private String studentName;
    private String className;
    private String sectionName;
    private String name;
    private Integer obtainedMarks;
    private Integer totalMaxMarks;
    private Double percentage;
    private String grade;
    private String resultStatus;
    private Integer classPosition;
    private String subjectName;
    private Integer theoryMarks;
    private Integer practicalMarks;
    private Integer vivaMarks;
    private Integer subjectObtainedMarks;
    private Double subjectPercentage;
    private String subjectGrade;
    private Integer totalObtainedMarks;
    private Double totalPercentage;
    private String overAllGrade;
    private String overResult;
    private Integer classId;
    private Integer sectionId;
    private Integer studentId;
    private Integer examTypeId;
    List<SubjectDetails> subjects;
}
