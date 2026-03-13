package com.sms.service.impl;

import com.sms.dao.HolidayDetailsDao;
import com.sms.model.HolidayDetails;
import com.sms.service.HolidayDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HolidayDetailsServiceImpl implements HolidayDetailsService {
    @Autowired
    private HolidayDetailsDao holidayDetailsDao;
    @Override
    public HolidayDetails addHolidayDetails(HolidayDetails holidayDetails, String schoolCode) throws Exception {
        return holidayDetailsDao.addHolidayDetails(holidayDetails, schoolCode);
    }

    @Override
    public List<HolidayDetails> getAllHolidayDetails(int sessionId,String schoolCode) throws Exception {
        return holidayDetailsDao.getAllHolidayDetails(sessionId,schoolCode);
    }

    @Override
    public List<HolidayDetails> getPastOrTodayHolidays(int sessionId, String schoolCode) throws Exception {
        return holidayDetailsDao.getPastOrTodayHolidays(sessionId,schoolCode);
    }

    @Override
    public HolidayDetails getHolidayDetailsById(int id,String schoolCode) {
        return holidayDetailsDao.getHolidayDetailsById(id,schoolCode);
    }

    @Override
    public HolidayDetails updateHolidayDetailsById(HolidayDetails holidayDetails, int id, String schoolCode) throws Exception {
        return holidayDetailsDao.updateHolidayDetailsById(holidayDetails,id, schoolCode);
    }

    @Override
    public boolean deleteHolidayDetails(int id, String schoolCode) throws Exception {
        return holidayDetailsDao.deleteHolidayDetails(id, schoolCode);
    }
}
