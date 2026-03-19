package com.sms.dao;

import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.model.ClassSubjectTeacherDetails;
import com.sms.model.SubjectDetails;

import java.util.List;

public interface ClassSubjectTeacherDao {
    public ClassSubjectTeacherDetails assignSubjectClassTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails, String schoolCode) throws Exception;

    public List<ClassSubjectTeacherDetails>ClassSubjectTeacher(String schoolCode) throws Exception;
    public ClassSubjectTeacherDetails getClassSubjectTeacherById(int cstaId, String schoolCode) throws Exception;
    public ClassSubjectTeacherDetails updateClassSubjectTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails,int cstaId, String schoolCode) throws Exception;
    public boolean deleteClassSubjectTeacher(int cstaId, String schoolCode) throws Exception;
    void bulkInsert(List<ClassSubjectTeacherDetails> allocations, String schoolCode);
    public List<ClassSubjectTeacherDetails>findAllocatedTeacher(int sessionId,int clasId, int sectionId, int subjectId,String schoolCode) throws Exception;

}
