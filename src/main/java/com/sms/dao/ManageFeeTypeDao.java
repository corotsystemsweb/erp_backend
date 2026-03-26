package com.sms.dao;

import com.sms.model.ManageFeeTypeDetails;

import java.util.List;

public interface ManageFeeTypeDao {
    List<ManageFeeTypeDetails> getManageFeeType(int sessionId, String schoolCode) throws Exception;
}
