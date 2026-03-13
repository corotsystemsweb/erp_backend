package com.sms.service;

import com.sms.model.HolidayDetails;

import java.util.List;

public interface HolidayDetailsService {
    public HolidayDetails addHolidayDetails(HolidayDetails holidayDetails, String schoolCode) throws Exception;

    public List<HolidayDetails> getAllHolidayDetails(int sessionId,String schoolCode) throws Exception;
    public List<HolidayDetails> getPastOrTodayHolidays(int sessionId,String schoolCode) throws Exception;

    public HolidayDetails getHolidayDetailsById(int id, String schoolCode);

    public HolidayDetails updateHolidayDetailsById(HolidayDetails holidayDetails, int id, String schoolCode) throws Exception;
    public boolean deleteHolidayDetails(int id, String schoolCode) throws Exception;

}
