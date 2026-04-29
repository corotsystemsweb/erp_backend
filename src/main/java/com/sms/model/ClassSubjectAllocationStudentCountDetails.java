package com.sms.model;

public class ClassSubjectAllocationStudentCountDetails {
    private Integer subjectId;
    private String subjectName;
    private Integer classId;
    private String className;
    private Integer sectionId;
    private String sectionName;
    private Integer studentCount;

    // Constructors
    public ClassSubjectAllocationStudentCountDetails() {}

    public ClassSubjectAllocationStudentCountDetails(Integer subjectId, String subjectName,
                                         Integer classId, String className,
                                         Integer sectionId, String sectionName,
                                         Integer studentCount) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.classId = classId;
        this.className = className;
        this.sectionId = sectionId;
        this.sectionName = sectionName;
        this.studentCount = studentCount;
    }

    // Getters and Setters
    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Integer getSectionId() {
        return sectionId;
    }

    public void setSectionId(Integer sectionId) {
        this.sectionId = sectionId;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public Integer getStudentCount() {
        return studentCount;
    }

    public void setStudentCount(Integer studentCount) {
        this.studentCount = studentCount;
    }
}