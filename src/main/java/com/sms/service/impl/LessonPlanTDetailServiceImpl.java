package com.sms.service.impl;

import com.sms.dao.LessonPlanTDetailDao;
import com.sms.model.LessonPlanTitleDetails;
import com.sms.service.LessonPlanTDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LessonPlanTDetailServiceImpl implements LessonPlanTDetailService {
    @Autowired
    private LessonPlanTDetailDao lessonPlanTDetailDao;
    @Transactional
    @Override
    public List<LessonPlanTitleDetails> createBulkDetails(List<LessonPlanTitleDetails> details, String schoolCode) throws Exception {
       /* for (LessonPlanTitleDetails  detail : details) {
            validateForeignKeys(detail, schoolCode);
            validateTeachingMethod(detail.getTeachingMethod());
        }*/

        return lessonPlanTDetailDao.saveAll(details, schoolCode);
    }
}
