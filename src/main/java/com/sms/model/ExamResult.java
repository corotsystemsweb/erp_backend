package com.sms.model;

public class ExamResult {
    private Long resultId;
    private Long studentId;
    private Long examSubjectId;
    private Integer theoryMarks;
    private Integer practicalMarks;
    private Integer vivaMarks;
    private Integer totalMarks;

    public Long getExamSubjectId() {
        return examSubjectId;
    }

    public void setExamSubjectId(Long examSubjectId) {
        this.examSubjectId = examSubjectId;
    }

    public Integer getPracticalMarks() {
        return practicalMarks;
    }

    public void setPracticalMarks(Integer practicalMarks) {
        this.practicalMarks = practicalMarks;
    }

    public Long getResultId() {
        return resultId;
    }

    public void setResultId(Long resultId) {
        this.resultId = resultId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getTheoryMarks() {
        return theoryMarks;
    }

    public void setTheoryMarks(Integer theoryMarks) {
        this.theoryMarks = theoryMarks;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getVivaMarks() {
        return vivaMarks;
    }

    public void setVivaMarks(Integer vivaMarks) {
        this.vivaMarks = vivaMarks;
    }
    // Getters and Setters
}
