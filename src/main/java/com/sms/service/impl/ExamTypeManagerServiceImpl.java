package com.sms.service.impl;

import com.sms.dao.ExamTypeManagerDao;
import com.sms.model.ExamTypeManager;
import com.sms.service.ExamTypeManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExamTypeManagerServiceImpl implements ExamTypeManagerService {
    @Autowired
    private ExamTypeManagerDao examTypeManagerDao;
    @Override
    public ExamTypeManager addExamType(ExamTypeManager examTypeManager, String schoolCode) throws Exception {
        return examTypeManagerDao.addExamType(examTypeManager,schoolCode);
    }

    @Override
    public List<ExamTypeManager> getAllExamType(int sessionId, String schoolCode) throws Exception {
        return examTypeManagerDao.getAllExamType(sessionId,schoolCode);
    }

    @Override
    public ExamTypeManager getExamTypeById( int examTypeId, String schoolCode) throws Exception {
        return examTypeManagerDao.getExamTypeById(examTypeId,schoolCode);
    }

    @Override
    public ExamTypeManager updateById(ExamTypeManager examTypeManager, int examTypeId, String schoolCode) throws Exception {
        return examTypeManagerDao.updateById(examTypeManager,examTypeId,schoolCode);
    }
}
