package com.sms.service;

import com.sms.model.FrequencyDetails;

import java.util.List;

public interface FrequencyService {
    public FrequencyDetails addFrequency(FrequencyDetails frequencyDetails, String schoolCode) throws Exception;
    public FrequencyDetails getFrequencyDetailsById(int frequencyId, String schoolCode) throws Exception;
    public List<FrequencyDetails> getAllFrequencyDetails(String schoolCode) throws Exception;
    public FrequencyDetails updateFrequencyDetailsById(FrequencyDetails frequencyDetails, int frequencyId, String schoolCode) throws Exception;
    public boolean deleteFrequency(int frequencyId, String schoolCode) throws Exception;
}
