package com.sms.model;

import java.util.List;

public class ClassAndSectionDetails {

    private int classSectionId;
    private int schoolId;
    private int classId;
    private int sectionId;   // keep this for single fetch use (optional)

    // ✅ NEW FIELD for multiple sections
    private List<Integer> sectionIds;

    private String studentClass;
    private String studentSection;
    private String schoolName;

    public int getClassSectionId() {
        return classSectionId;
    }

    public void setClassSectionId(int classSectionId) {
        this.classSectionId = classSectionId;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }

    // ✅ Getter & Setter for multiple sectionIds
    public List<Integer> getSectionIds() {
        return sectionIds;
    }

    public void setSectionIds(List<Integer> sectionIds) {
        this.sectionIds = sectionIds;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public String getStudentSection() {
        return studentSection;
    }

    public void setStudentSection(String studentSection) {
        this.studentSection = studentSection;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
