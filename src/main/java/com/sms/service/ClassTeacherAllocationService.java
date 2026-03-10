package com.sms.service;

import com.sms.model.ClassTeacherAllocationDetails;

import java.util.List;

public interface ClassTeacherAllocationService {
    public ClassTeacherAllocationDetails addClassTeacherAllocation(ClassTeacherAllocationDetails classTeacherAllocationDetails, String schoolCode) throws Exception;
    public ClassTeacherAllocationDetails getClassTeacherAllocationById(int ctaId, String schoolCode) throws Exception;
    public List<ClassTeacherAllocationDetails> getAllClassTeacherAllocation(String schoolCode) throws Exception;
    public ClassTeacherAllocationDetails updateClassTeacherAllocationById(ClassTeacherAllocationDetails classTeacherAllocationDetails, int ctaId, String schoolCode) throws Exception;
    public boolean deleteClassTeacherAllocationById(int ctaId, String schoolCode) throws Exception;
    public List<ClassTeacherAllocationDetails> findAllTeacherByClassAndSection(int sessionId,int classId, int sectionId, String schoolCode) throws Exception;


}
