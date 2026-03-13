/*
package com.sms.dao;

import com.sms.model.FeeAssignmentDetailsNew;

public interface FeeAsWithDDateDao {
    boolean assignFeeWithDueDates(FeeAssignmentDetailsNew feeAssignmentDetailsNew,String schoolCode);
}
*/
package com.sms.dao;

import com.sms.model.FeeAssignmentDetailsNew;

import java.util.List;

public interface FeeAsWithDDateDao {
    boolean assignFeeWithDueDates(FeeAssignmentDetailsNew details, String schoolCode);
/*    boolean existsClassAssignment(String schoolCode, int schoolId, int sessionId, int classId, int feeId);
    boolean existsSectionAssignment(String schoolCode, int schoolId, int sessionId, int classId, int sectionId, int feeId);
    boolean existsStudentAssignment(String schoolCode, int schoolId, int sessionId, int studentId, int feeId);*/
    FeeAssignmentDetailsNew getFeeAssignmentDetailsWithStudents(long faId,String schoolCode);
    List<FeeAssignmentDetailsNew> getAllFeeAssignments(String schoolCode,Integer classId, Integer sectionId, Integer studentId);
    void updateFeeAssignment(FeeAssignmentDetailsNew updatedAssignment, String schoolCode);

}