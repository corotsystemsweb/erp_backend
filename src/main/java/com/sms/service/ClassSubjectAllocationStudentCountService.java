package com.sms.service;

import com.sms.model.ClassSubjectAllocationDetails;

import java.util.List;

public interface ClassSubjectAllocationStudentCountService {

    public List<ClassSubjectAllocationDetails> getSubjectAllocationsWithStudentCount(
            int sessionId,
            Integer classId,
            Integer sectionId,
            Integer subjectId,
            String schoolCode
    ) throws Exception;

    public List<ClassSubjectAllocationDetails> getAllSubjectAllocationsWithStudentCount(
            int sessionId,
            String schoolCode
    ) throws Exception;

    public Integer getStudentCountForAllocation(
            int subjectId,
            int classId,
            int sectionId,
            int sessionId,
            String schoolCode
    ) throws Exception;
}