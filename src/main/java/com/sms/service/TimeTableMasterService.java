package com.sms.service;

import com.sms.model.TimetableMaster;

public interface TimeTableMasterService {
    int createTimeTableMaster(TimetableMaster master, String schoolCode) throws Exception;
}
