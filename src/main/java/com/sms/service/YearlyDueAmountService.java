package com.sms.service;

import com.sms.model.YearlyDueAmountDetails;

import java.util.List;

public interface YearlyDueAmountService {
    List<YearlyDueAmountDetails> calculateYearlyDueAmounts(int sessionId, String schoolCode) throws Exception;
}
