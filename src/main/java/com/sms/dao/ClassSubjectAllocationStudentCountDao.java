package com.sms.dao;

import com.sms.model.ClassSubjectAllocationDetails;
import java.util.List;

public interface ClassSubjectAllocationStudentCountDao {

    // Flexible method - can accept null for optional parameters
    public List<ClassSubjectAllocationDetails> getSubjectAllocationsWithStudentCount(
            int sessionId,
            Integer classId,
            Integer sectionId,
            Integer subjectId,
            String schoolCode
    ) throws Exception;

    // Convenience method - get all allocations for a session
    public List<ClassSubjectAllocationDetails> getAllSubjectAllocationsWithStudentCount(
            int sessionId,
            String schoolCode
    ) throws Exception;

    // Convenience method - get student count for specific allocation
    public Integer getStudentCountForAllocation(
            int subjectId,
            int classId,
            int sectionId,
            int sessionId,
            String schoolCode
    ) throws Exception;
}