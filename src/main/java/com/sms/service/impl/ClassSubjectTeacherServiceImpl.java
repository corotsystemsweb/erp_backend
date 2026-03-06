package com.sms.service.impl;

import com.sms.dao.ClassSubjectTeacherDao;
import com.sms.model.ClassSubjectTeacherDetails;
import com.sms.service.ClassSubjectTeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClassSubjectTeacherServiceImpl implements ClassSubjectTeacherService {
    @Autowired
    private ClassSubjectTeacherDao classSubjectTeacherDao;
    @Override
    public ClassSubjectTeacherDetails assignSubjectClassTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails, String schoolCode) throws Exception {
        return classSubjectTeacherDao.assignSubjectClassTeacher(classSubjectTeacherDetails,schoolCode);
    }

    @Override
    public List<ClassSubjectTeacherDetails>ClassSubjectTeacher(String schoolCode) throws Exception {
        return classSubjectTeacherDao.ClassSubjectTeacher(schoolCode);
    }

    @Override
    public ClassSubjectTeacherDetails getClassSubjectTeacherById(int cstaId,String schoolCode) throws Exception {
        return classSubjectTeacherDao.getClassSubjectTeacherById(cstaId, schoolCode);
    }

    @Override
    public ClassSubjectTeacherDetails updateClassSubjectTeacher(ClassSubjectTeacherDetails classSubjectTeacherDetails, int cstaId, String schoolCode) throws Exception {
        return classSubjectTeacherDao.updateClassSubjectTeacher(classSubjectTeacherDetails,cstaId, schoolCode);
    }

    @Override
    public boolean deleteClassSubjectTeacher(int cstaId, String schoolCode) throws Exception {
        return classSubjectTeacherDao.deleteClassSubjectTeacher(cstaId,schoolCode);
    }

    @Override
    @Transactional
    public void bulkInsertAllocations(List<ClassSubjectTeacherDetails> classSubjectTeacherDetails, String schoolCode) {
        if (classSubjectTeacherDetails == null || classSubjectTeacherDetails.isEmpty()) {
            throw new IllegalArgumentException("Allocations list cannot be empty");
        }
        classSubjectTeacherDao.bulkInsert(classSubjectTeacherDetails,schoolCode);
    }

    @Override
    public List<ClassSubjectTeacherDetails> findAllocatedTeacher(int sessionId, int classId, int sectionId, int subjectId, String schoolCode) throws Exception {
        return classSubjectTeacherDao.findAllocatedTeacher(sessionId,classId,sectionId,subjectId,schoolCode);
    }
}
