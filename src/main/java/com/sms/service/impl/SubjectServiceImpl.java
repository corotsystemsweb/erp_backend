package com.sms.service.impl;

import com.sms.dao.SubjectDao;
import com.sms.model.SubjectDetails;
import com.sms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectDao subjectDao;
    @Override
    public SubjectDetails addSubject(SubjectDetails subjectDetails, String schoolCode) throws Exception {
        return subjectDao.addSubject(subjectDetails, schoolCode);
    }

    @Override
    public SubjectDetails getSubjectDetailsById(int subjectId, String schoolCode) throws Exception {
        return subjectDao.getSubjectDetailsById(subjectId,schoolCode);
    }

    @Override
    public List<SubjectDetails> getAllSubjectDetails(String schoolCode) throws Exception {
        return subjectDao.getAllSubjectDetails(schoolCode);
    }

    @Override
    public SubjectDetails updateSubjectDetailsById(SubjectDetails subjectDetails, int subjectId, String schoolCode) throws Exception {
        return subjectDao.updateSubjectDetailsById(subjectDetails, subjectId, schoolCode);
    }

    @Override
    public boolean deleteSubject(int subjectId, String schoolCode) throws Exception {
        return subjectDao.deleteSubject(subjectId,schoolCode);
    }

}
