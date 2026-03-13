package com.sms.service.impl;

import com.sms.dao.ClassDao;
import com.sms.model.ClassDetails;
import com.sms.service.ClassService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ClassServiceImpl implements ClassService {
    @Autowired
    private ClassDao classDao;
    @Override
    public ClassDetails addClass(ClassDetails classDetails,String schoolCode) throws Exception {
        return classDao.addClass(classDetails,schoolCode);
    }

    @Override
    public ClassDetails getClassDetailsById(int classId,String schoolCode) throws Exception {
        return classDao.getClassDetailsById(classId,schoolCode);
    }

    @Override
    public List<ClassDetails> getAllClassDetails(String schoolCode) throws Exception {
        return classDao.getAllClassDetails(schoolCode);
    }

    @Override
    public ClassDetails updateClassDetailsById(ClassDetails classDetails, int classId,String schoolCode) throws Exception {
        return classDao.updateClassDetailsById(classDetails, classId, schoolCode);
    }

    @Override
    public boolean deleteClass(int classId,String schoolCode) throws Exception {
        return classDao.deleteClass(classId,schoolCode);
    }
}
