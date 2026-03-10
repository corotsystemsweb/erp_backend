package com.sms.dao.impl;

import com.sms.dao.ClassAttendanceDao;
import com.sms.model.ClassAttendanceDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Map;
@Repository
public class ClassAttendanceDaoImpl implements ClassAttendanceDao {
    private final JdbcTemplate jdbcTemplate;

    public ClassAttendanceDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

   /* @Override
    public ClassAttendanceDetails addClassAttendance(ClassAttendanceDetails classAttendanceDetails) throws Exception {
     //   String sql="insert into class_attendance(class_id,section_id,subject_id,teacher_id,present_date,attendance_mark_time) values(?,?,?,?,?,?)";
        String sql="insert into class_attendance(class_id,section_id,subject_id,teacher_id,present_date,attendance_mark_time) values ((select class_id from mst_class where class_name=?),(select section_id from mst_section where section_name=?),?,?,?,?)";
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,classAttendanceDetails.getClassId());
                ps.setInt(2,classAttendanceDetails.getSectionId());
                ps.setInt(3,classAttendanceDetails.getSubjectId());
                ps.setInt(4,classAttendanceDetails.getTeacherId());
                ps.setDate(5,classAttendanceDetails.getPresentDate());
                ps.setTimestamp(6, new Timestamp(classAttendanceDetails.getAttendanceMarkTime().getTime()));
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("ca_id")){
                int generatedId=((Number) keys.get("ca_id")).intValue();
                classAttendanceDetails.setCaId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        return classAttendanceDetails ;
    }*/

  /*  @Override
    public ClassAttendanceDetails addClassAttendance(ClassAttendanceDetails classAttendanceDetails) throws Exception {
          String sql="insert into class_attendance(class_id,section_id,subject_id,teacher_id,present_date,attendance_mark_time) values(?,?,?,?,?,?)";
       // String sql="insert into class_attendance(class_id,section_id,subject_id,teacher_id,present_date,attendance_mark_time) values ((select class_id from mst_class where class_name=?),(select section_id from mst_section where section_name=?),?,?,?,?)";
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,classAttendanceDetails.getClassId());
                ps.setInt(2,classAttendanceDetails.getSectionId());
                ps.setInt(3,classAttendanceDetails.getSubjectId());
                ps.setInt(4,classAttendanceDetails.getTeacherId());
                ps.setDate(5,classAttendanceDetails.getPresentDate());
                ps.setTimestamp(6, new Timestamp(classAttendanceDetails.getAttendanceMarkTime().getTime()));
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("ca_id")){
                int generatedId=((Number) keys.get("ca_id")).intValue();
                classAttendanceDetails.setCaId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
        return classAttendanceDetails ;
    }*/
  @Override
  public ClassAttendanceDetails addClassAttendance(ClassAttendanceDetails classAttendanceDetails, String schoolCode) throws Exception {
      String sql="insert into class_attendance(class_id,section_id,subject_id,present_date,attendance_mark_time) values(?,?,?,?,?)";
      // String sql="insert into class_attendance(class_id,section_id,subject_id,teacher_id,present_date,attendance_mark_time) values ((select class_id from mst_class where class_name=?),(select section_id from mst_section where section_name=?),?,?,?,?)";
      JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
      try{
          KeyHolder keyHolder=new GeneratedKeyHolder();
          jdbcTemplate.update(connection -> {
              PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
              ps.setInt(1,classAttendanceDetails.getClassId());
              ps.setInt(2,classAttendanceDetails.getSectionId());
              ps.setInt(3,classAttendanceDetails.getSubjectId());
              ps.setDate(4,classAttendanceDetails.getPresentDate());
              ps.setTimestamp(5, new Timestamp(classAttendanceDetails.getAttendanceMarkTime().getTime()));
              return ps;
          },keyHolder);
          Map<String,Object> keys=keyHolder.getKeys();
          if(keys != null && keys.containsKey("ca_id")){
              int generatedId=((Number) keys.get("ca_id")).intValue();
              classAttendanceDetails.setCaId(generatedId);
          }
      }catch(Exception e) {
          e.printStackTrace();
          return null;
      } finally {
          DatabaseUtil.closeDataSource(jdbcTemplate);
      }
      return classAttendanceDetails ;
  }
    @Override
    public int getClassAttendanceId(int classId,int sectionId,int subjectId,int teacherId,Date presentDate, String schoolCode) throws Exception {
        String sql = "SELECT ca.ca_id " +
                "FROM class_attendance AS ca " +
                "WHERE ca.class_id = ? " +
                "AND ca.section_id = ? " +
                "AND ca.subject_id = ? " +
                "AND ca.teacher_id = ? " +
                "AND ca.present_date = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, Integer.class, classId,sectionId,subjectId,teacherId,presentDate);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
