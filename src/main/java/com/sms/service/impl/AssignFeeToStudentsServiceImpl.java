package com.sms.service.impl;

import com.sms.dao.AssignFeeToStudentsDao;
import com.sms.model.AssignFeeToStudentsDetails;
import com.sms.service.AssignFeeToStudentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssignFeeToStudentsServiceImpl implements AssignFeeToStudentsService {

    @Autowired
    private AssignFeeToStudentsDao assignFeeToStudentsDao;


    @Override
    public List<AssignFeeToStudentsDetails> getAssignFeeToStudentDetails(int sessionId, String schoolCode) throws Exception {
        return assignFeeToStudentsDao.getAssignFeeToStudentDetails(sessionId, schoolCode);
    }
}
