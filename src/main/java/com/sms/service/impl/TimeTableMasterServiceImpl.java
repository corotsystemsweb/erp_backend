package com.sms.service.impl;

import com.sms.dao.TimetableMasterDao;
import com.sms.model.TimetableMaster;
import com.sms.service.TimeTableMasterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeTableMasterServiceImpl implements TimeTableMasterService {
    @Autowired
    private TimetableMasterDao timetableMasterDao;

    @Override
    public int createTimeTableMaster(TimetableMaster master, String schoolCode) throws Exception {
        return timetableMasterDao.createTimeTable(master, schoolCode);
    }
}
