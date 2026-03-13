package com.sms.dao;

import com.sms.model.ClassSubjectAllocationDetails;

import java.util.List;

public interface ClassSubjectAllocationDao {
    public List<ClassSubjectAllocationDetails> addAllocateSubject(List<ClassSubjectAllocationDetails> classSubjectAllocationDetailsList, String schoolCode) throws Exception;

    public List<ClassSubjectAllocationDetails>getAllAlocatedSubject(String schoolCode) throws Exception;

    public ClassSubjectAllocationDetails getAllocatedClassSubjectById(int csaId, String schoolCode) throws Exception;

    public ClassSubjectAllocationDetails updateAllocatedSubject(ClassSubjectAllocationDetails classSubjectAllocationDetails,int csaId,String schoolCode) throws Exception;

    public boolean deleteAllocatedSubject(int csaId, String schoolCode) throws Exception;

    public List<ClassSubjectAllocationDetails> findSubject(int classId,int sectionId, int sessionId, String schoolCode) throws Exception;
    void deleteByClassSection(int schoolId, int sessionId, int classId, int sectionId, String schoolCode);
    void insertSubjects(int schoolId, int sessionId, int classId, int sectionId, List<Integer> subjectIds, String schoolCode);


}
