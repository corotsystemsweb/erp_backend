package com.sms.dao;

import com.sms.model.TimetableMaster;

public interface TimetableMasterDao {
    int createTimeTable(TimetableMaster master, String schoolCode) throws Exception;
}
