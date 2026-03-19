package com.sms.service;

import com.sms.model.ClassSubjectAllocationDetails;

import java.util.List;
import java.util.Map;

public interface ClassSubjectAllocationService {
    public List<ClassSubjectAllocationDetails> addAllocateSubject(List<ClassSubjectAllocationDetails> classSubjectAllocationDetailsList, String schoolCode) throws Exception;

    public List<ClassSubjectAllocationDetails>getAllAlocatedSubject(String schoolCode) throws Exception;
    public ClassSubjectAllocationDetails getAllocatedClassSubjectById(int csaId, String schoolCode) throws Exception;
    public ClassSubjectAllocationDetails updateAllocatedSubject(ClassSubjectAllocationDetails classSubjectAllocationDetails,int csaId,String schoolCode) throws Exception;
    public boolean deleteAllocatedSubject(int csaId, String schoolCode) throws Exception;
    public List<ClassSubjectAllocationDetails> findSubject(int classId,int sectionId, int sessionId, String SchoolCode) throws Exception;
    List<Map<String, Object>> getClassSectionSubjectStructure(String schoolCode) throws Exception;
    void updateAllocatedSubjects(int schoolId, int sessionId, int classId, int sectionId, List<Integer> subjectIds, String schoolCode);
}
