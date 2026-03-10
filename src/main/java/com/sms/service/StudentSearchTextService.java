package com.sms.service;

import com.sms.model.StudentSearchTextDetails;

import java.util.List;

public interface StudentSearchTextService {
    public List<StudentSearchTextDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception;
    public List<StudentSearchTextDetails> getStudentFeeDetailsBasedOnClassSectionSessionForSearchText(int classId, int sectionId, int sessionId, String searchText, String schoolCode) throws Exception;
    public List<StudentSearchTextDetails> getFeeDatilsBasedOnSession(int sessionId, String searchText, String schoolCode) throws Exception;

}
