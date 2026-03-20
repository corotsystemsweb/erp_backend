package com.sms.service.impl;

import com.sms.dao.CreateTimetableDao;
import com.sms.dao.TimetableMasterDao;
import com.sms.model.TimetableDetails;
import com.sms.model.TimetableMaster;
import com.sms.service.TimetableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
public class TimetableServiceImpl implements TimetableService {
    @Autowired
    private CreateTimetableDao createTimetableDao;

    @Autowired
    private TimetableMasterDao timetableMasterDao;



    @Override
    public TimetableDetails addTimetableEntry(TimetableDetails timetable,String schoolCode) throws SQLException {
        return createTimetableDao.createTimetable(timetable,schoolCode);
    }

    @Transactional
    @Override
    public String createTimetableBulkWithMaster(List<TimetableDetails> timeTables, String schoolCode) throws Exception {

        if (timeTables == null || timeTables.isEmpty()) {
            throw new RuntimeException("Timetable data is empty");
        }

        Map<String, List<TimetableDetails>> grouped = timeTables.stream().collect(Collectors.groupingBy(t -> {
                            if (t.getDayOfWeek() == null) {
                                throw new RuntimeException("Day of week is required");
                            }
                            return t.getDayOfWeek().toUpperCase();
                        }));

        for (Map.Entry<String, List<TimetableDetails>> entry : grouped.entrySet()) {

            List<TimetableDetails> dayList = entry.getValue();
            TimetableDetails first = dayList.get(0);

            //Validation (same class/section/session)
            boolean valid = dayList.stream().allMatch(t ->
                    t.getSchoolId() == first.getSchoolId() &&
                            t.getSessionId() == first.getSessionId() &&
                            t.getClassId() == first.getClassId() &&
                            t.getSectionId() == first.getSectionId()
            );

            if (!valid) {
                throw new RuntimeException("Invalid data: mixed class/section/session in same day");
            }

            // Date validation
            if (first.getTimeTableDate() == null) {
                throw new RuntimeException("Timetable date is required for " + first.getDayOfWeek());
            }


            //Insert into master
            TimetableMaster master = new TimetableMaster();
            master.setSchoolId(first.getSchoolId());
            master.setSessionId(first.getSessionId());
            master.setClassId(first.getClassId());
            master.setSectionId(first.getSectionId());
            master.setDayOfWeek(first.getDayOfWeek());
            master.setTimetableDate(first.getTimeTableDate());
            master.setUpdatedBy(first.getUpdatedBy());

            int masterId = timetableMasterDao.createTimeTable(master, schoolCode);

            //safety check
            if (masterId <= 0) {
                throw new RuntimeException("Failed to insert timetable master");
            }

            //Insert CHILD (batch)
            List<TimetableDetails> result = createTimetableDao.createTimetableBulkWithMaster(dayList, masterId, schoolCode);
            if (result == null || result.isEmpty()) {
                throw new RuntimeException("Failed to insert timetable details");
            }
        }
        return "Timetable added successfully";
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

    @Override
    public List<TimetableDetails> getAllTimeTableBasedOnStaffId(int sessionId, Integer staffId, String schoolCode) throws Exception {
        return createTimetableDao.getAllTimeTableBasedOnStaffId(sessionId, staffId, schoolCode);
    }

    @Override
    public List<TimetableDetails> getAllTimeTableSchedule(int sessionId, String schoolCode) throws Exception {
        return createTimetableDao.getAllTimeTableSchedule(sessionId, schoolCode);
    }

    @Override
    public TimetableDetails updateTimetable(TimetableDetails timetableDetails, String schoolCode) throws Exception {
        return createTimetableDao.updateTimetable(timetableDetails, schoolCode);
    }

    @Override
    public String deleteTimetable(int timetableId, String schoolCode) throws Exception {
        return createTimetableDao.deleteTimetable(timetableId, schoolCode);
    }

}
