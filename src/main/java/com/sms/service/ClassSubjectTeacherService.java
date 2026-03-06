package com.sms.service;

import com.sms.model.ClassSubjectTeacherDetails;

import java.util.List;

public interface ClassSubjectTeacherService {
    public ClassSubjectTeacherDetails assignSubjectClassTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails, String schoolCode) throws Exception;
    public List<ClassSubjectTeacherDetails>ClassSubjectTeacher(String schoolCode) throws Exception;
    public ClassSubjectTeacherDetails getClassSubjectTeacherById(int cstaId, String schoolCode) throws Exception;
    public ClassSubjectTeacherDetails updateClassSubjectTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails,int cstaId, String schoolCode) throws Exception;
    public boolean deleteClassSubjectTeacher(int cstaId, String schoolCode) throws Exception;
    void bulkInsertAllocations(List<ClassSubjectTeacherDetails> allocations,String schoolCode);
    public List<ClassSubjectTeacherDetails>findAllocatedTeacher(int sessionId,int classId, int sectionId, int subjectId,String schoolCode) throws Exception;

}
