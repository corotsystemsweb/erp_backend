package com.sms.service;

import com.sms.model.ManageFeeTypeDetails;

import java.util.List;

public interface ManageFeeTypeService {
    List<ManageFeeTypeDetails> getManageFeeType(int sessionId, String schoolCode) throws Exception;
}
