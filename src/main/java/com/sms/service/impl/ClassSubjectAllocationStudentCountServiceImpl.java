package com.sms.service.impl;

import com.sms.dao.ClassSubjectAllocationStudentCountDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.service.ClassSubjectAllocationStudentCountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassSubjectAllocationStudentCountServiceImpl implements ClassSubjectAllocationStudentCountService {

    @Autowired
    private ClassSubjectAllocationStudentCountDao classSubjectAllocationStudentCountDao;

    @Override
    public List<ClassSubjectAllocationDetails> getSubjectAllocationsWithStudentCount(
            int sessionId, Integer classId, Integer sectionId, Integer subjectId, String schoolCode)
            throws Exception {

        try {
            return classSubjectAllocationStudentCountDao.getSubjectAllocationsWithStudentCount(
                    sessionId, classId, sectionId, subjectId, schoolCode);
        } catch (Exception e) {
            throw new Exception("Error in service layer while getting subject allocations: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ClassSubjectAllocationDetails> getAllSubjectAllocationsWithStudentCount(
            int sessionId, String schoolCode) throws Exception {

        try {
            return classSubjectAllocationStudentCountDao.getAllSubjectAllocationsWithStudentCount(sessionId, schoolCode);
        } catch (Exception e) {
            throw new Exception("Error in service layer while getting all subject allocations: " + e.getMessage(), e);
        }
    }

    @Override
    public Integer getStudentCountForAllocation(
            int subjectId, int classId, int sectionId, int sessionId, String schoolCode)
            throws Exception {

        try {
            return classSubjectAllocationStudentCountDao.getStudentCountForAllocation(
                    subjectId, classId, sectionId, sessionId, schoolCode);
        } catch (Exception e) {
            throw new Exception("Error in service layer while getting student count: " + e.getMessage(), e);
        }
    }
}