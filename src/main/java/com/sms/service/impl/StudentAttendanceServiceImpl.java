package com.sms.service.impl;

import com.sms.dao.StudentAttendanceDao;
import com.sms.model.StudentAttendanceDetails;
import com.sms.service.StudentAttendanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class StudentAttendanceServiceImpl implements StudentAttendanceService {
    @Autowired
    private StudentAttendanceDao studentAttendanceDao;

    @Override
    public List<StudentAttendanceDetails> addAttendance(List<StudentAttendanceDetails> studentAttendanceDetails, String schoolCode) throws Exception {
        return studentAttendanceDao.addAttendance(studentAttendanceDetails, schoolCode);
    }
    /*@Override
    public List<StudentAttendanceDetails> getTotalAttendance(int studentId, int classId, int sectionId, int subjectId, Date dateFrom, Date dateTo) throws Exception {
        return studentAttendanceDao.getTotalAttendance(studentId, classId, sectionId, subjectId, dateFrom, dateTo);
    }*/
    @Override
    public List<StudentAttendanceDetails> getTotalAttendance(Integer studentId, Integer subjectId, Date dateFrom, Date dateTo, String schoolCode) throws Exception {
        return studentAttendanceDao.getTotalAttendance(studentId, subjectId, dateFrom, dateTo, schoolCode);
    }
}



