package com.sms.model;
import lombok.Data;

import java.util.List;
@Data
public class FeeAssignmentDetailsNew {
    private long faId;
    private int schoolId;
    private int sessionId;
    private String academicSession;
    private String className;
    private String sectionName;
    private String studentName;
    private String feeType;
    private String dcDescription;
    private Integer classId;
    private Integer sectionId;
    private Integer studentId;
    private int feeId;
    private Integer dcId;
    private String validFrom;
    private String validTo;
    private int updatedBy;
    private List<FeeDueDateDetailsNew> dueDates;
    private List<StudentFeeStatus> students;
    private boolean hasDeposit;
    private String assignedStudentGender;
    private String AssignedStudentType;
}
