package com.sms.dao;

import com.sms.model.TimetableDetails;

import java.sql.SQLException;
import java.util.List;

public interface CreateTimetableDao {
    public TimetableDetails createTimetable(TimetableDetails timetable,String schoolCode) throws SQLException;
    List<TimetableDetails> createTimetableBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException;
    List<TimetableDetails> getAllTimeTableBasedClassSection(int classId,int sectionId,int sessionId, String schoolCode) throws Exception;
    public int[] updateTimetableBulk(List<TimetableDetails> timetables, String schoolCode) throws SQLException;
    public int[] deleteTimetableBulk(List<Integer> timetableIds, String schoolCode) throws SQLException;
}
