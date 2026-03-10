package com.sms.service.impl;

import com.sms.dao.ClassTeacherAllocationDao;
import com.sms.model.ClassTeacherAllocationDetails;
import com.sms.service.ClassTeacherAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassTeacherAllocationServiceImpl implements ClassTeacherAllocationService {
    @Autowired
    ClassTeacherAllocationDao classTeacherAllocationDao;
    @Override
    public ClassTeacherAllocationDetails addClassTeacherAllocation(ClassTeacherAllocationDetails classTeacherAllocationDetails,String schoolCode) throws Exception {
        return classTeacherAllocationDao.addClassTeacherAllocation(classTeacherAllocationDetails,schoolCode);
    }

    @Override
    public ClassTeacherAllocationDetails getClassTeacherAllocationById(int ctaId, String schoolCode) throws Exception {
        return classTeacherAllocationDao.getClassTeacherAllocationById(ctaId, schoolCode);
    }

    @Override
    public List<ClassTeacherAllocationDetails> getAllClassTeacherAllocation(String schoolCode) throws Exception {
        return classTeacherAllocationDao.getAllClassTeacherAllocation(schoolCode);
    }

    @Override
    public ClassTeacherAllocationDetails updateClassTeacherAllocationById(ClassTeacherAllocationDetails classTeacherAllocationDetails, int ctaId, String schoolCode) throws Exception {
        return classTeacherAllocationDao.updateClassTeacherAllocationById(classTeacherAllocationDetails, ctaId, schoolCode);
    }

    @Override
    public boolean deleteClassTeacherAllocationById(int ctaId, String schoolCode) throws Exception {
        return classTeacherAllocationDao.deleteClassTeacherAllocationById(ctaId, schoolCode);
    }

    @Override
    public List<ClassTeacherAllocationDetails> findAllTeacherByClassAndSection(int sessionId, int classId, int sectionId, String schoolCode) throws Exception {
        return classTeacherAllocationDao.findAllTeacherByClassAndSection(sessionId,classId,sectionId,schoolCode);
    }
}
