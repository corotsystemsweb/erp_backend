package com.sms.dao;

import com.sms.model.LessonPlanDetails;

import java.util.List;

public interface LessonPlanDao {
    LessonPlanDetails save(LessonPlanDetails lessonPlanDetails,String schoolCode) throws Exception;
    public LessonPlanDetails getById(int lessonPlanId, String schoolCode) throws Exception;
    public List<LessonPlanDetails> getAllLessionPlan(String schoolCode) throws Exception;
    public int update(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception;
}
