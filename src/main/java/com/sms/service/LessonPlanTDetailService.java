package com.sms.service;

import com.sms.model.LessonPlanTitleDetails;

import java.util.List;

public interface LessonPlanTDetailService {
    public List<LessonPlanTitleDetails> createBulkDetails(List<LessonPlanTitleDetails> details, String schoolCode) throws Exception;


}
