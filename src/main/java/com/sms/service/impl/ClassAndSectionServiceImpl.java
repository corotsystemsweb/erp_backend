package com.sms.service.impl;

import com.sms.dao.ClassAndSectionDao;
import com.sms.model.ClassAndSectionDetails;
import com.sms.service.ClassAndSectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ClassAndSectionServiceImpl implements ClassAndSectionService {
    @Autowired
    private ClassAndSectionDao classAndSectionDao;
    @Override
    public ClassAndSectionDetails addClassAndSection(ClassAndSectionDetails classAndSectionDetails, String schoolCode) throws Exception {
        return classAndSectionDao.addClassAndSection(classAndSectionDetails, schoolCode);
    }

    @Override
    public ClassAndSectionDetails getClassAndSectionById(int classSectionId, String SchoolCode) throws Exception {
        return classAndSectionDao.getClassAndSectionById(classSectionId, SchoolCode);
    }

    @Override
    public List<ClassAndSectionDetails> getAllClassAndSection(String schoolCode) throws Exception {
        return classAndSectionDao.getAllClassAndSection(schoolCode);
    }

    @Override
    public ClassAndSectionDetails updateClassAndSection(ClassAndSectionDetails classAndSectionDetails, int classSectionId, String schoolCode) throws Exception {
        return classAndSectionDao.updateClassAndSection(classAndSectionDetails, classSectionId, schoolCode);
    }

    @Override
    public boolean deleteClassAndSection(int classSectionId, String schoolCode) throws Exception {
        return classAndSectionDao.deleteClassAndSection(classSectionId, schoolCode);
    }

    @Override
    public List<ClassAndSectionDetails> getClassRelatedSection(String schoolCode) throws Exception {
        return classAndSectionDao.getClassRelatedSection(schoolCode);
    }
}
