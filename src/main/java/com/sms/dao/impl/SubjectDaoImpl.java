package com.sms.dao.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sms.dao.SubjectDao;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Repository
public class SubjectDaoImpl implements SubjectDao {
    @Value("${subject.syllabus.local.path}")
    private String SYLLABUS_FOLDER_PATH;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SubjectDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final ObjectMapper objectMapper = new ObjectMapper();

   private int findNextAvailableId(String schoolCode) {
       String dbsql = "SELECT COALESCE(MAX(subject_id), 0) + 1 AS subject_id FROM mst_subject";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       // Initialize id to 0 in case there are no records
       int nextAvailableId = 0;

       try {
           // Execute the SQL query using jdbcTemplate
           nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
       } catch (EmptyResultDataAccessException e) {
           e.printStackTrace();
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return nextAvailableId;
   }
    /*
    @method addSubject
    @param subjectDetails
    @throws Exception
    @description adding subject details in subject table
    @developer Sukhendu Bhowmik
    */


    @Override
    public boolean uploadSyllabus(MultipartFile file, String schoolCode, int subjectId) throws Exception {

        long maxSize = 10 * 1024 * 1024;

        if(file.getSize() > maxSize){
            throw new RuntimeException("File size exceeds 10MB limit");
        }

        String originalName = file.getOriginalFilename();

        if(originalName == null || !originalName.contains(".")){
            throw new RuntimeException("Invalid file name");
        }

        String extension = originalName.substring(originalName.lastIndexOf(".") + 1).toLowerCase();

        if (!extension.equals("pdf") && !extension.equals("doc") && !extension.equals("docx")
                && !extension.equals("jpeg") && !extension.equals("jpg") && !extension.equals("png")) {

            throw new RuntimeException("Only PDF, Word, JPG, JPEG, and PNG files allowed");
        }

        String fileName = subjectId + "." + extension;

        File directory = new File(SYLLABUS_FOLDER_PATH + schoolCode);

        if (!directory.exists()) {
            directory.mkdirs();
        }

        // delete old syllabus files
        File[] oldFiles = directory.listFiles((dir, name) -> name.startsWith(subjectId + "."));

        if(oldFiles != null){
            for(File oldFile : oldFiles){
                oldFile.delete();
            }
        }

        String path = directory.getAbsolutePath() + File.separator + fileName;

        file.transferTo(new File(path));

        return true;
    }

    @Override
    public SubjectDetails getSyllabus(String schoolCode, int subjectId) throws Exception {

        File folder = new File(SYLLABUS_FOLDER_PATH + schoolCode);

        if (!folder.exists()) {
            return null;
        }

        File[] files = folder.listFiles();

        if(files == null){
            return null;
        }

        for (File file : files) {

            if (file.getName().startsWith(subjectId + ".")) {

                byte[] fileBytes = Files.readAllBytes(file.toPath());

                String base64File = Base64.getEncoder().encodeToString(fileBytes);

                SubjectDetails subjectDetails = new SubjectDetails();

                subjectDetails.setSyllabus(base64File);
                subjectDetails.setSyllabusFileName(file.getName());

                return subjectDetails;
            }
        }

        return null;
    }

    @Override
   public SubjectDetails addSubject(SubjectDetails subjectDetails, String schoolCode) throws Exception {
       int nextAvailableId = findNextAvailableId(schoolCode);
       subjectDetails.setSubjectId(nextAvailableId);
       String sql = "insert into mst_subject (subject_id,school_id,subject_name,subject_code,subject_category,weekly_hours,subject_description,has_practical_exam,elective_course,allow_last_enrollment,auto_grading,grade_weightage, status) values(?,?,?,?,?,?,?,?,?,?,?,?::jsonb,?)";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           String gradeWeightageJson = objectMapper.writeValueAsString(subjectDetails.getGradeWeightage());
           jdbcTemplate.update(sql,
                   subjectDetails.getSubjectId(),
                   subjectDetails.getSchoolId(),
                   subjectDetails.getSubjectName(),
                   subjectDetails.getSubjectCode(),
                   subjectDetails.getSubjectCategory(),
                   subjectDetails.getWeeklyHours(),
                   subjectDetails.getSubjectDescription(),
                   subjectDetails.isHasPracticalExam(),
                   subjectDetails.isElectiveCourse(),
                   subjectDetails.isAllowLastEnrollment(),
                   subjectDetails.isAutoGrading(),
                   gradeWeightageJson,
                   subjectDetails.getStatus()
           );
       }catch(Exception e){
           //e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

       return subjectDetails;
   }
    /*
    @method getSubjectDetailsById
    @param subjectId
    @throws Exception
    @description getting subject details by id from subject table
    @developer Sukhendu Bhowmik
    */
    @Override
    public SubjectDetails getSubjectDetailsById(int subjectId,String schoolCode) throws Exception {
        String sql = """
                SELECT
                    ms.subject_id,
                    ms.school_id,
                    ms.subject_name,
                    ms.subject_code,
                    ms.subject_category,
                    ms.weekly_hours,
                    ms.subject_description,
                    ms.has_practical_exam,
                    ms.elective_course,
                    ms.allow_last_enrollment,
                    ms.auto_grading,
                    ms.grade_weightage,
                    ms.status,

                    COUNT(DISTINCT csta.teacher_id) AS faculty,

                    COUNT(DISTINCT (csta.class_id, csta.section_id))
                        FILTER (WHERE csta.class_id IS NOT NULL) AS classes

                FROM mst_subject ms

                LEFT JOIN class_subject_teacher_allocation csta
                    ON ms.subject_id = csta.subject_id
                    AND ms.school_id = csta.school_id
                where ms.subject_id = ?
                GROUP BY
                    ms.subject_id,
                    ms.school_id,
                    ms.subject_name,
                    ms.subject_code,
                    ms.subject_category,
                    ms.weekly_hours,
                    ms.subject_description,
                    ms.has_practical_exam,
                    ms.elective_course,
                    ms.allow_last_enrollment,
                    ms.auto_grading,
                    ms.grade_weightage,
                    ms.status

                ORDER BY ms.subject_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        SubjectDetails subjectDetails = null;
        try{
            subjectDetails = jdbcTemplate.queryForObject(sql, new Object[]{subjectId}, new RowMapper<SubjectDetails>() {
                @Override
                public SubjectDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SubjectDetails sd = new SubjectDetails();
                    sd.setSubjectId(rs.getInt("subject_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSubjectName(rs.getString("subject_name"));
                    sd.setSubjectCode(rs.getString("subject_code"));
                    sd.setSubjectCategory(rs.getString("subject_category"));
                    sd.setWeeklyHours(rs.getDouble("weekly_hours"));
                    sd.setSubjectDescription(rs.getString("subject_description"));
                    sd.setHasPracticalExam(rs.getBoolean("has_practical_exam"));
                    sd.setElectiveCourse(rs.getBoolean("elective_course"));
                    sd.setAllowLastEnrollment(rs.getBoolean("allow_last_enrollment"));
                    sd.setAutoGrading(rs.getBoolean("auto_grading"));
                    String gradeWeightageJson = rs.getString("grade_weightage");

                    if (gradeWeightageJson != null) {
                        try {
                            Map<String, Integer> map = objectMapper.readValue(gradeWeightageJson, new TypeReference<Map<String, Integer>>() {});
                            sd.setGradeWeightage(map);

                        } catch (Exception e) {
                            throw new SQLException("Error parsing grade_weightage JSON", e);
                        }
                    }
                    sd.setStatus(rs.getString("status"));
                    sd.setFacultyCount(rs.getInt("faculty"));
                    sd.setClassCount(rs.getInt("classes"));

                    return sd;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return subjectDetails;
    }
    /*
    @method getAllSubjectDetails
    @throws Exception
    @description getting all subject details from subject table
    @developer Sukhendu Bhowmik
    */

    @Override
    public List<SubjectDetails> getAllSubjectDetails(String schoolCode) throws Exception {
        String sql = """
                SELECT\s
                    ms.subject_id,
                    ms.school_id,
                    ms.subject_name,
                    ms.subject_code,
                    ms.subject_category,
                    ms.weekly_hours,
                    ms.subject_description,
                    ms.has_practical_exam,
                    ms.elective_course,
                    ms.allow_last_enrollment,
                    ms.auto_grading,
                    ms.grade_weightage,
                    ms.status,
                
                    COUNT(DISTINCT csta.teacher_id) AS faculty,
                
                    COUNT(DISTINCT (csta.class_id, csta.section_id))\s
                        FILTER (WHERE csta.class_id IS NOT NULL) AS classes
                
                FROM mst_subject ms
                
                LEFT JOIN class_subject_teacher_allocation csta
                    ON ms.subject_id = csta.subject_id
                    AND ms.school_id = csta.school_id
                
                GROUP BY\s
                    ms.subject_id,
                    ms.school_id,
                    ms.subject_name,
                    ms.subject_code,
                    ms.subject_category,
                    ms.weekly_hours,
                    ms.subject_description,
                    ms.has_practical_exam,
                    ms.elective_course,
                    ms.allow_last_enrollment,
                    ms.auto_grading,
                    ms.grade_weightage,
                    ms.status
                
                ORDER BY ms.subject_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<SubjectDetails> subjectDetails = null;
        try{
            subjectDetails = jdbcTemplate.query(sql, new RowMapper<SubjectDetails>() {
                @Override
                public SubjectDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    SubjectDetails sd = new SubjectDetails();
                    sd.setSubjectId(rs.getInt("subject_id"));
                    sd.setSchoolId(rs.getInt("school_id"));
                    sd.setSubjectName(rs.getString("subject_name"));
                    sd.setSubjectCode(rs.getString("subject_code"));
                    sd.setSubjectCategory(rs.getString("subject_category"));
                    sd.setWeeklyHours(rs.getDouble("weekly_hours"));
                    sd.setSubjectDescription(rs.getString("subject_description"));
                    sd.setHasPracticalExam(rs.getBoolean("has_practical_exam"));
                    sd.setElectiveCourse(rs.getBoolean("elective_course"));
                    sd.setAllowLastEnrollment(rs.getBoolean("allow_last_enrollment"));
                    sd.setAutoGrading(rs.getBoolean("auto_grading"));
                    String gradeWeightageJson = rs.getString("grade_weightage");

                    if (gradeWeightageJson != null) {
                        try {
                            Map<String, Integer> map = objectMapper.readValue(gradeWeightageJson, new TypeReference<Map<String, Integer>>() {});
                            sd.setGradeWeightage(map);

                        } catch (Exception e) {
                            throw new SQLException("Error parsing grade_weightage JSON", e);
                        }
                    }
                    sd.setStatus(rs.getString("status"));
                    sd.setFacultyCount(rs.getInt("faculty"));
                    sd.setClassCount(rs.getInt("classes"));

                    return sd;
                }
            });
        }catch (Exception e){
            throw new Exception("Error fetching subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return subjectDetails;
    }
    /*
    @method updateSubjectDetailsById
    @param subjectDetails, subjectId
    @throws Exception
    @description update subject details by id
    @developer Sukhendu Bhowmik
    */

    //check  the subject name which has already exists or not. If I update English to Hindi but Hindi is already there then it will give false
    public boolean isOtherSubjectHasSameName(SubjectDetails subjectDetails, int subjectId, String schoolCode){
        String checkSql = "SELECT COUNT(*) FROM mst_subject WHERE subject_id != ? AND subject_name = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, subjectId, subjectDetails.getSubjectName());
            return count != null && count > 0;
        }catch (EmptyResultDataAccessException e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public SubjectDetails updateSubjectDetailsById(SubjectDetails subjectDetails, int subjectId, String schoolCode) throws Exception {
        if(isOtherSubjectHasSameName(subjectDetails, subjectId, schoolCode)){
            return null;
        }
        // If no conflict, proceed with the update
        String sql = "UPDATE mst_subject SET school_id = ?, subject_name = ?, subject_code=?, subject_category=?, weekly_hours=?, subject_description=?, has_practical_exam=?, elective_course=?, allow_last_enrollment=?, auto_grading=?, grade_weightage=?::jsonb, status=? WHERE subject_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            String gradeWeightageJson = null;
            if(subjectDetails.getGradeWeightage() != null){
                gradeWeightageJson = objectMapper.writeValueAsString(subjectDetails.getGradeWeightage());
            }
            int rowEffected = jdbcTemplate.update(sql,
                    subjectDetails.getSchoolId(),
                    subjectDetails.getSubjectName(),
                    subjectDetails.getSubjectCode(),
                    subjectDetails.getSubjectCategory(),
                    subjectDetails.getWeeklyHours(),
                    subjectDetails.getSubjectDescription(),
                    subjectDetails.isHasPracticalExam(),
                    subjectDetails.isElectiveCourse(),
                    subjectDetails.isAllowLastEnrollment(),
                    subjectDetails.isAutoGrading(),
                    gradeWeightageJson,
                    subjectDetails.getStatus(),
                    subjectId
            );

            if (rowEffected > 0) {
                return subjectDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new Exception("Error updating subject details", e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

    }


    /*
    @method deleteSubject
    @param subjectId
    @throws Exception
    @description delete subject details by id
    @developer Sukhendu Bhowmik
    */

   @Override
   public boolean deleteSubject(int subjectId, String schoolCode) throws Exception {
       String sql = "delete from mst_subject where subject_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql, new Object[]{subjectId});
           if(rowAffected > 0){
               return true;
           }else{
               return false;
           }
       } catch (Exception e) {
           e.printStackTrace();
           return false;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
   }

    @Override
    public String softDeleteSubject(int subjectId, String schoolCode) throws Exception {
        String sql = "UPDATE mst_subject SET status = 'Inactive' WHERE subject_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql, subjectId);
            if(rowEffected > 0){
                return "Subject deleted successfully";
            } else {
                return "Subject is not deleted";
            }
        } catch (Exception e){
            throw new Exception("Error while soft deleting subject", e);
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}
