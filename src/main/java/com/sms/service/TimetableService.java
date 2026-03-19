package com.sms.service;

import com.sms.model.TimetableDetails;

import java.sql.SQLException;
import java.util.List;

public interface TimetableService {
    public TimetableDetails addTimetableEntry(TimetableDetails timetable,String schoolCode) throws SQLException;
    String createTimetableBulkWithMaster(List<TimetableDetails> timeTables, String schoolCode) throws Exception;
    List<TimetableDetails> getAllTimeTableBasedClassSection(int classId,int sectionId,int sessionId, String schoolCode) throws Exception;
    public int[] updateTimetableBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException;
    public int[] deleteTimetableBulk(List<Integer> timetableIds, String schoolCode) throws SQLException;
    public List<TimetableDetails> getAllTimeTableBasedOnStaffId(int sessionId, Integer staffId, String schoolCode) throws Exception;
    public List<TimetableDetails> getAllTimeTableSchedule(int sessionId, String schoolCode) throws Exception;
}
