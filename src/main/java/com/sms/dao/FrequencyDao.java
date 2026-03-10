package com.sms.dao;

import com.sms.model.FrequencyDetails;
import com.sms.model.SubjectDetails;

import java.util.List;

public interface FrequencyDao {
    public FrequencyDetails addFrequency(FrequencyDetails frequencyDetails, String schoolCode) throws Exception;
    public FrequencyDetails getFrequencyDetailsById(int frequencyId, String schoolCode) throws Exception;
    public List<FrequencyDetails> getAllFrequencyDetails(String schoolCode) throws Exception;
    public FrequencyDetails updateFrequencyDetailsById(FrequencyDetails frequencyDetails, int frequencyId, String schoolCode) throws Exception;
    public boolean deleteFrequency(int frequencyId, String schoolCode) throws Exception;
}
