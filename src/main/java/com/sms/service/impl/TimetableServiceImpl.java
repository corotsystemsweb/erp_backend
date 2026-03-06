package com.sms.service.impl;

import com.sms.dao.CreateTimetableDao;
import com.sms.model.TimetableDetails;
import com.sms.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
public class TimetableServiceImpl implements TimetableService {
    @Autowired
    private CreateTimetableDao createTimetableDao;



    @Override
    public TimetableDetails addTimetableEntry(TimetableDetails timetable,String schoolCode) throws SQLException {
        return createTimetableDao.createTimetable(timetable,schoolCode);
    }

    @Override
    public List<TimetableDetails> addTimetableEntriesBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException {
        return createTimetableDao.createTimetableBulk(timetables, schoolCode);
    }

    @Override
    public List<TimetableDetails> getAllTimeTableBasedClassSection(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        return createTimetableDao.getAllTimeTableBasedClassSection(classId,sectionId,sessionId,schoolCode);
    }
    @Transactional
    @Override
    public int[] updateTimetableBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException {
        return createTimetableDao.updateTimetableBulk(timetables,schoolCode);
    }

    @Override
    public int[] deleteTimetableBulk(List<Integer> timetableIds, String schoolCode) throws SQLException {
        return createTimetableDao.deleteTimetableBulk(timetableIds,schoolCode);
    }
}
