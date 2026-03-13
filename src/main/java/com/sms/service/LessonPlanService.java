package com.sms.service;

import com.sms.model.LessonPlanDetails;

import java.util.List;

public interface LessonPlanService {
    LessonPlanDetails save(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception;
    public LessonPlanDetails getLessonPlan(int id, String schoolCode) throws Exception;
    public List<LessonPlanDetails> getAllLessionPlan(String schoolCode) throws Exception;
    public int update(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception;

}
