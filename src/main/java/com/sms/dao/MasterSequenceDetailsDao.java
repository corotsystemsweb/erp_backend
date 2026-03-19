package com.sms.dao;

import com.sms.model.MasterSequenceDetails;

public interface MasterSequenceDetailsDao {
    public void addSeqCodeAndCurrentValue(String schoolCode) throws Exception;
}
