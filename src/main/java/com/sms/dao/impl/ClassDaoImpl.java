/*
package com.sms.dao.impl;

import com.sms.dao.ClassDao;
import com.sms.model.ClassDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
@Repository
public class ClassDaoImpl implements ClassDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public ClassDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(class_id), 0) + 1 AS class_id FROM mst_class";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        // Initialize id to 0 in case there are no records
        int nextAvailableId = 0;

        try {
            // Execute the SQL query using jdbcTemplate
            nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return 0;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableId;
    }

    */
/*
    @method addClass
    @param classDetails
    @throws Exception
    @description adding class details in mst_class table
    @developer Sukhendu Bhowmik
    *//*


    @Override
    public ClassDetails addClass(ClassDetails classDetails,String schoolCode) throws Exception {
        int nextAvailableId = findNextAvailableId(schoolCode);
        classDetails.setClassId(nextAvailableId);
        // SQL query to insert a new record with a specified ID
        String sql = "insert into mst_class (class_id, school_id, class_name) values(?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            // Execute the SQL query using jdbcTemplate
            jdbcTemplate.update(sql, classDetails.getClassId(), classDetails.getSchoolId(), classDetails.getStudentClass());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classDetails;
    }

    */
/*
   @method getClassDetailsById
   @param classId
   @throws Exception
   @description getting class details by id from mst_class table
   @developer Sukhendu Bhowmik
   *//*

    @Override
    public ClassDetails getClassDetailsById(int classId,String schoolCode) throws Exception {
        String sql = "select distinct class_id, school_id, class_name from mst_class where class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ClassDetails classDetails = null;
       try{
           classDetails = jdbcTemplate.queryForObject(sql, new Object[]{classId}, new RowMapper<ClassDetails>() {
               @Override
               public ClassDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   ClassDetails cd = new ClassDetails();
                   cd.setClassId(rs.getInt("class_id"));
                   cd.setSchoolId(rs.getInt("school_id"));
                   cd.setStudentClass(rs.getString("class_name"));
                   return cd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
        return classDetails;
    }
    */
/*
    @method getAllClassDetails
    @throws Exception
    @description getting all class details from mst_class table
    @developer Sukhendu Bhowmik
    *//*


    @Override
    public List<ClassDetails> getAllClassDetails(String schoolCode) throws Exception {
        String sql = "select distinct class_id, school_id, class_name from mst_class order by class_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassDetails> classDetails=null;
        try{
        classDetails = jdbcTemplate.query(sql, new RowMapper<ClassDetails>() {
            @Override
            public ClassDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                ClassDetails cd = new ClassDetails();
                cd.setClassId(rs.getInt("class_id"));
                cd.setSchoolId(rs.getInt("school_id"));
                cd.setStudentClass(rs.getString("class_name"));
                return cd;
            }
        });
    }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classDetails;
        }

    */
/*
    @method updateSubjectDetailsById
    @param subjectDetails, classId
    @throws Exception
    @description update class details by id
    @developer Sukhendu Bhowmik
    *//*


    @Override
    public ClassDetails updateClassDetailsById(ClassDetails classDetails, int classId,String schoolCode) throws Exception {
        String sql = "UPDATE mst_class SET school_id = ?, class_name = ? WHERE class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowEffected = jdbcTemplate.update(sql,
                   classDetails.getSchoolId(),
                   classDetails.getStudentClass(),
                   classDetails.getClassId());
           if(rowEffected > 0){
               return classDetails;
           }else{
               return null;
           }
       }catch(Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
    }
    */
/*
   @method deleteSubject
   @param classId
   @throws Exception
   @description delete class details by id
   @developer Sukhendu Bhowmik
   *//*

    @Override
    public boolean deleteClass(int classId,String schoolCode) throws Exception {
        String sql = "delete from mst_class where class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        int rowAffected = jdbcTemplate.update(sql, new Object[]{classId});
        if(rowAffected > 0){
            return true;
        }else{
            return false;
        }
    }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }
}
*/


package com.sms.dao.impl;

import com.sms.dao.ClassDao;
import com.sms.model.ClassDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ClassDaoImpl implements ClassDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ClassDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private int findNextAvailableId(String schoolCode) {
        String dbsql = "SELECT COALESCE(MAX(class_id), 0) + 1 AS class_id FROM mst_class";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        int nextAvailableId = 0;

        try {
            nextAvailableId = jdbcTemplate.queryForObject(dbsql, Integer.class);
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return 0;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return nextAvailableId;
    }

    @Override
    public ClassDetails addClass(ClassDetails classDetails, String schoolCode) throws Exception {
        int nextAvailableId = findNextAvailableId(schoolCode);
        classDetails.setClassId(nextAvailableId);
        String sql = "INSERT INTO mst_class (class_id, school_id, class_name) VALUES (?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            jdbcTemplate.update(sql, classDetails.getClassId(), classDetails.getSchoolId(), classDetails.getStudentClass());
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classDetails;
    }

    @Override
    public ClassDetails getClassDetailsById(int classId, String schoolCode) throws Exception {
        String sql = "SELECT DISTINCT class_id, school_id, class_name FROM mst_class WHERE class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        ClassDetails classDetails = null;

        try {
            classDetails = jdbcTemplate.queryForObject(sql, new Object[]{classId}, new RowMapper<ClassDetails>() {
                @Override
                public ClassDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassDetails cd = new ClassDetails();
                    cd.setClassId(rs.getInt("class_id"));
                    cd.setSchoolId(rs.getInt("school_id"));
                    cd.setStudentClass(rs.getString("class_name"));
                    return cd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classDetails;
    }

    @Override
    public List<ClassDetails> getAllClassDetails(String schoolCode) throws Exception {
        String sql = "SELECT DISTINCT class_id, school_id, class_name FROM mst_class ORDER BY class_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<ClassDetails> classDetails;

        try {
            classDetails = jdbcTemplate.query(sql, new RowMapper<ClassDetails>() {
                @Override
                public ClassDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    ClassDetails cd = new ClassDetails();
                    cd.setClassId(rs.getInt("class_id"));
                    cd.setSchoolId(rs.getInt("school_id"));
                    cd.setStudentClass(rs.getString("class_name"));
                    return cd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return classDetails;
    }

    @Override
    public ClassDetails updateClassDetailsById(ClassDetails classDetails, int classId, String schoolCode) throws Exception {
        String sql = "UPDATE mst_class SET school_id = ?, class_name = ? WHERE class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            int rowsAffected = jdbcTemplate.update(sql, classDetails.getSchoolId(), classDetails.getStudentClass(), classId);
            if (rowsAffected > 0) {
                return classDetails;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean deleteClass(int classId, String schoolCode) throws Exception {
        String sql = "DELETE FROM mst_class WHERE class_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            int rowsAffected = jdbcTemplate.update(sql, new Object[]{classId});
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e; // Re-throw the exception for higher-level handling
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}

