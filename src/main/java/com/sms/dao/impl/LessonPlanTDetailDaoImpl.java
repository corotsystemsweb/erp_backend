package com.sms.dao.impl;

import com.sms.dao.LessonPlanTDetailDao;
import com.sms.model.LessonPlanTitleDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class LessonPlanTDetailDaoImpl implements LessonPlanTDetailDao {

    private static final Logger LOGGER = Logger.getLogger(LessonPlanTDetailDaoImpl.class.getName());

    private static final String INSERT_SQL = "INSERT INTO lesson_plan_details "
            + "(lesson_plan_id, school_id, session_id, topic_name, sub_topic_name, "
            + "lesson_date, resources, teaching_method) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    @Override
    public List<LessonPlanTitleDetails> saveAll(List<LessonPlanTitleDetails> lessonPlanTitleDetails, String schoolCode) throws Exception {
        try {
            JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

            int[] batchResult = jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    LessonPlanTitleDetails detail = lessonPlanTitleDetails.get(i);
                    ps.setInt(1, detail.getLessonPlanId());
                    ps.setInt(2, detail.getSchoolId());
                    ps.setInt(3, detail.getSessionId());
                    ps.setString(4, detail.getTopicName());
                    ps.setString(5, detail.getSubTopicName());
                    ps.setDate(6, new java.sql.Date(detail.getLessonDate().getTime()));
                    ps.setString(7, detail.getResources());
                    ps.setString(8, detail.getTeachingMethod());
                }

                @Override
                public int getBatchSize() {
                    return lessonPlanTitleDetails.size();
                }
            });

            LOGGER.info("Inserted " + batchResult.length + " records into lesson_plan_details");
            return lessonPlanTitleDetails;  // Return the input list after successful insertion

        } catch (Exception e) {
            LOGGER.severe("Bulk insert failed: " + e.getMessage());
            throw new Exception("Bulk insert failed", e);
        }
    }

    @Override
    public LessonPlanTitleDetails findById(int detailId, String schoolCode) throws Exception {
        return null;
    }
}
