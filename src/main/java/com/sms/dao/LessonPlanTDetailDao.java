package com.sms.dao;

import com.sms.model.LessonPlanTitleDetails;

import java.util.List;

public interface LessonPlanTDetailDao {
    List<LessonPlanTitleDetails> saveAll(List<LessonPlanTitleDetails> lessonPlanTitleDetails, String schoolCode) throws Exception;
    LessonPlanTitleDetails findById(int detailId, String schoolCode) throws Exception;
}
