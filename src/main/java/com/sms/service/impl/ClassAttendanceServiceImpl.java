package com.sms.service.impl;

import com.sms.dao.ClassAttendanceDao;
import com.sms.model.ClassAttendanceDetails;
import com.sms.service.ClassAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;

@Service
public class ClassAttendanceServiceImpl implements ClassAttendanceService {
    @Autowired
    ClassAttendanceDao classAttendanceDao;
    @Override
    public ClassAttendanceDetails addClassAttendance(ClassAttendanceDetails classAttendanceDetails, String schoolCode) throws Exception {
        return classAttendanceDao.addClassAttendance(classAttendanceDetails, schoolCode);
    }

    @Override
    public int getClassAttendanceId(int classId,int sectionId,int subjectId,int teacherId,Date presentDate, String schoolCode) throws Exception {
        return classAttendanceDao.getClassAttendanceId(classId,sectionId,subjectId,teacherId,presentDate, schoolCode);
    }
}
