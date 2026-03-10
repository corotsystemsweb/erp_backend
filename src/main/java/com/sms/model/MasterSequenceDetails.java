package com.sms.model;

public class MasterSequenceDetails {
    private int id;
    private int schoolId;
    private int seqCode;
    private int currentValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public int getSeqCode() {
        return seqCode;
    }

    public void setSeqCode(int seqCode) {
        this.seqCode = seqCode;
    }

    public int getCurrentValue() {
        return currentValue;
    }

    public void setCurrentValue(int currentValue) {
        this.currentValue = currentValue;
    }
}
