package com.sms.service.impl;

import com.sms.dao.LessonPlanDao;
import com.sms.model.LessonPlanDetails;
import com.sms.service.LessonPlanService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonPlanServiceImpl implements LessonPlanService {
    private final LessonPlanDao lessonPlanDao;

    public LessonPlanServiceImpl(LessonPlanDao lessonPlanDao) {
        this.lessonPlanDao = lessonPlanDao;
    }

    @Override
    public LessonPlanDetails save(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception {
        return lessonPlanDao.save(lessonPlanDetails,schoolCode);
    }

    @Override
    public LessonPlanDetails getLessonPlan(int id, String schoolCode) throws Exception {
        return lessonPlanDao.getById(id,schoolCode);
    }

    @Override
    public List<LessonPlanDetails> getAllLessionPlan(String schoolCode) throws Exception {
        return lessonPlanDao.getAllLessionPlan(schoolCode);
    }

    @Override
    public int update(LessonPlanDetails lessonPlanDetails, String schoolCode) throws Exception {
        return lessonPlanDao.update(lessonPlanDetails,schoolCode);
    }
}
