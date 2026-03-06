package com.sms.dao.impl;

import com.sms.dao.StudentFeeCollectionDao;
import com.sms.model.StudentFeeCollectionDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Repository
public class StudentFeeCollectionDaoImpl implements StudentFeeCollectionDao {
    private final JdbcTemplate jdbcTemplate;

    public StudentFeeCollectionDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<StudentFeeCollectionDetails> getAllStudentFeeCollection(String schoolCode) throws Exception {
        String sql = "WITH FeeDetails AS (\n" +
                "    SELECT\n" +
                "        spd.student_id,\n" +
                "        fs.fee_type,\n" +
                "        fdd.fee_amount,\n" +
                "        fdd.discount_amount,\n" +
                "        COUNT(fdd.fee_amount) AS fee_count,\n" +
                "        SUM(fdd.fee_amount) AS total_fee_amount,\n" +
                "        SUM(fdd.discount_amount) AS total_discount_amount,\n" +
                "        fa.session_id\n" +
                "    FROM\n" +
                "        student_personal_details spd\n" +
                "    JOIN\n" +
                "        student_academic_details sad ON spd.student_id = sad.student_id AND spd.school_id = sad.school_id\n" +
                "    JOIN\n" +
                "        mst_class mc ON sad.student_class_id = mc.class_id\n" +
                "    JOIN\n" +
                "        mst_section ms ON sad.student_section_id = ms.section_id\n" +
                "    JOIN\n" +
                "        school_details sd ON spd.school_id = sd.school_id\n" +
                "    JOIN\n" +
                "        fee_assignment fa ON fa.school_id = spd.school_id\n" +
                "        AND (\n" +
                "            (fa.class_id = mc.class_id AND fa.section_id = 0 AND fa.student_id = 0)\n" +
                "            OR (fa.class_id = mc.class_id AND fa.section_id = ms.section_id AND fa.student_id = 0)\n" +
                "            OR (fa.student_id = spd.student_id)\n" +
                "        )\n" +
                "    JOIN\n" +
                "        fee_due_date fdd ON fa.fa_id = fdd.fa_id\n" +
                "    JOIN\n" +
                "        school_fees fs ON fs.fee_id = fa.fee_id\n" +
                "    WHERE\n" +
                "        spd.deleted IS NOT TRUE\n" +
                "        AND sad.session_id = fa.session_id -- Ensure fees are for the same session as the student's academic session\n" +
                "    GROUP BY\n" +
                "        spd.student_id, fs.fee_type, fdd.fee_amount, fdd.discount_amount, fa.session_id\n" +
                ")\n" +
                "SELECT\n" +
                "    sd.school_id,\n" +
                "    sd.school_name,\n" +
                "    spd.student_id,\n" +
                "    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,\n" +
                "    mc.class_id,\n" +
                "    mc.class_name,\n" +
                "    ms.section_id,\n" +
                "    ms.section_name,\n" +
                "    ses.session_id,\n" +
                "    ses.academic_session,\n" +
                "    COALESCE(\n" +
                "        ARRAY_AGG(\n" +
                "            DISTINCT CONCAT(\n" +
                "                'Fee Type: ', fd.fee_type,\n" +
                "                ', Fee Amount: ',\n" +
                "                    CASE\n" +
                "                        WHEN fd.fee_count > 1 THEN CONCAT(fd.fee_count, '*', fd.fee_amount::text, ' = ', (fd.fee_count * fd.fee_amount)::text)\n" +
                "                        ELSE fd.total_fee_amount::text\n" +
                "                    END,\n" +
                "                ', Discount Amount: ', fd.total_discount_amount::text\n" +
                "            )\n" +
                "        ),\n" +
                "        ARRAY[]::text[]\n" +
                "    ) AS fee_details,\n" +
                "    COALESCE(SUM(fd.total_fee_amount), 0) AS total_fee_assigned,\n" +
                "    COALESCE(SUM(fd.total_discount_amount), 0) AS total_discount,\n" +
                "    COALESCE(SUM(fd.total_fee_amount), 0) - COALESCE(SUM(fd.total_discount_amount), 0) AS gross_student_fee\n" +
                "FROM\n" +
                "    student_personal_details spd\n" +
                "JOIN\n" +
                "    student_academic_details sad ON spd.student_id = sad.student_id AND spd.school_id = sad.school_id\n" +
                "JOIN\n" +
                "    mst_class mc ON sad.student_class_id = mc.class_id\n" +
                "JOIN\n" +
                "    mst_section ms ON sad.student_section_id = ms.section_id\n" +
                "JOIN\n" +
                "    school_details sd ON spd.school_id = sd.school_id\n" +
                "JOIN\n" +
                "    session ses ON sad.session_id = ses.session_id\n" +
                "LEFT JOIN\n" +
                "    FeeDetails fd ON spd.student_id = fd.student_id\n" +
                "    AND sad.session_id = fd.session_id -- Ensure fees are for the same session as the student's academic session\n" +
                "WHERE\n" +
                "    spd.deleted IS NOT TRUE -- Filter out deleted students\n" +
                "GROUP BY\n" +
                "    spd.student_id, spd.first_name, spd.last_name, sd.school_id, sd.school_name, mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session\n" +
                "ORDER BY\n" +
                "    mc.class_id, ms.section_id asc";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<StudentFeeCollectionDetails> studentFeeCollectionDetails = null;
        try{
            studentFeeCollectionDetails = jdbcTemplate.query(sql, new RowMapper<StudentFeeCollectionDetails>() {
                @Override
                public StudentFeeCollectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    StudentFeeCollectionDetails sfcd = new StudentFeeCollectionDetails();
                    sfcd.setSchoolId(rs.getInt("school_id"));
                    sfcd.setSchoolName(rs.getString("school_name"));
                    sfcd.setStudentId(rs.getInt("student_id"));
                    sfcd.setStudentName(rs.getString("student_name"));
                    sfcd.setClassId(rs.getInt("class_id"));
                    sfcd.setClassName(rs.getString("class_name"));
                    sfcd.setSectionId(rs.getInt("section_id"));
                    sfcd.setSectionName(rs.getString("section_name"));
                    sfcd.setSessionId(rs.getInt("session_id"));
                    sfcd.setSessionName(rs.getString("academic_session"));
                    sfcd.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                    sfcd.setTotalDiscount(rs.getDouble("total_discount"));
                    sfcd.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                    sfcd.setFeeDetails(Arrays.asList((String[])rs.getArray("fee_details").getArray()));
                    return sfcd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return studentFeeCollectionDetails;
    }

   @Override
   public StudentFeeCollectionDetails getStudentFeeCollectionByStudentId(int studentId, String schoolCode) throws Exception {
       String sql = " WITH FeeDetails AS (\n" +
               "    SELECT\n" +
               "        spd.student_id,\n" +
               "        fs.fee_type,\n" +
               "        fdd.fee_amount,\n" +
               "        fdd.discount_amount,\n" +
               "        COUNT(fdd.fee_amount) AS fee_count,\n" +
               "        SUM(fdd.fee_amount) AS total_fee_amount,\n" +
               "        SUM(fdd.discount_amount) AS total_discount_amount\n" +
               "    FROM\n" +
               "        student_personal_details spd\n" +
               "    JOIN\n" +
               "        student_academic_details sad ON spd.student_id = sad.student_id AND spd.school_id = sad.school_id\n" +
               "    JOIN\n" +
               "        mst_class mc ON sad.student_class_id = mc.class_id\n" +
               "    JOIN\n" +
               "        mst_section ms ON sad.student_section_id = ms.section_id\n" +
               "    JOIN\n" +
               "        school_details sd ON spd.school_id = sd.school_id\n" +
               "    JOIN\n" +
               "        fee_assignment fa ON fa.school_id = spd.school_id\n" +
               "        AND (\n" +
               "            (fa.class_id = mc.class_id AND fa.section_id = 0 AND fa.student_id = 0)\n" +
               "            OR (fa.class_id = mc.class_id AND fa.section_id = ms.section_id AND fa.student_id = 0)\n" +
               "            OR (fa.student_id = spd.student_id)\n" +
               "        )\n" +
               "    JOIN\n" +
               "        fee_due_date fdd ON fa.fa_id = fdd.fa_id\n" +
               "    JOIN\n" +
               "        school_fees fs ON fs.fee_id = fa.fee_id\n" +
               "    WHERE\n" +
               "        spd.deleted IS NOT TRUE\n" +
               "    GROUP BY\n" +
               "        spd.student_id, fs.fee_type, fdd.fee_amount, fdd.discount_amount\n" +
               ")\n" +
               "SELECT\n" +
               "    sd.school_id,\n" +
               "    sd.school_name,\n" +
               "    sd.school_building,\n" +
               "    sd.school_address,\n" +
               "    sd.email_address,\n" +
               "    sd.school_city,\n" +
               "    sd.school_state,\n" +
               "    sd.school_country,\n" +
               "    sd.school_zipcode,\n" +
               "    spd.student_id,\n" +
               "    CONCAT(spd.first_name, ' ', spd.last_name) AS student_name,\n" +
               "    mc.class_id,\n" +
               "    mc.class_name,\n" +
               "    ms.section_id,\n" +
               "    ms.section_name,\n" +
               "    ses.session_id,\n" +
               "    ses.academic_session,\n" +
               "    ARRAY_AGG(\n" +
               "        DISTINCT CONCAT(\n" +
               "            'Fee Type: ', fd.fee_type,\n" +
               "            ', Fee Amount: ',\n" +
               "                CASE \n" +
               "                    WHEN fd.fee_count > 1 THEN CONCAT(fd.fee_count, '*', fd.fee_amount::text, ' = ', (fd.fee_count * fd.fee_amount)::text)\n" +
               "                    ELSE fd.total_fee_amount::text\n" +
               "                END,\n" +
               "            ', Discount Amount: ', fd.total_discount_amount::text\n" +
               "        )\n" +
               "    ) AS fee_details,\n" +
               "    COALESCE(SUM(fd.total_fee_amount), 0) AS total_fee_assigned,\n" +
               "    COALESCE(SUM(fd.total_discount_amount), 0) AS total_discount,\n" +
               "    COALESCE(SUM(fd.total_fee_amount), 0) - COALESCE(SUM(fd.total_discount_amount), 0) AS gross_student_fee\n" +
               "FROM\n" +
               "    student_personal_details spd\n" +
               "JOIN\n" +
               "    student_academic_details sad ON spd.student_id = sad.student_id AND spd.school_id = sad.school_id\n" +
               "JOIN\n" +
               "    mst_class mc ON sad.student_class_id = mc.class_id\n" +
               "JOIN\n" +
               "    mst_section ms ON sad.student_section_id = ms.section_id\n" +
               "JOIN\n" +
               "    school_details sd ON spd.school_id = sd.school_id\n" +
               "JOIN\n" +
               "    session ses ON sad.session_id = ses.session_id\n" +
               "JOIN\n" +
               "    FeeDetails fd ON spd.student_id = fd.student_id\n" +
               "WHERE spd.student_id = ?\n" +
               "GROUP BY\n" +
               "    spd.student_id, spd.first_name, spd.last_name, sd.school_id, sd.school_name, sd.school_building, sd.school_address, sd.email_address, sd.school_city, sd.school_state, sd.school_country, sd.school_zipcode, mc.class_id, mc.class_name, ms.section_id, ms.section_name, ses.session_id, ses.academic_session\n" +
               "ORDER BY\n" +
               "    mc.class_id, ms.section_id asc";

       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       StudentFeeCollectionDetails studentFeeCollectionDetails = null;
       try{
           studentFeeCollectionDetails = jdbcTemplate.queryForObject(sql, new Object[]{studentId}, new RowMapper<StudentFeeCollectionDetails>() {
               @Override
               public StudentFeeCollectionDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   StudentFeeCollectionDetails sfcd = new StudentFeeCollectionDetails();
                   sfcd.setSchoolId(rs.getInt("school_id"));
                   sfcd.setSchoolName(rs.getString("school_name"));
                   sfcd.setSchoolBuilding(rs.getString("school_building"));
                   sfcd.setSchoolAddress(rs.getString("school_address"));
                   sfcd.setEmailAddress(rs.getString("email_address"));
                   sfcd.setSchoolCity(rs.getString("school_city"));
                   sfcd.setSchoolState(rs.getString("school_state"));
                   sfcd.setSchoolCountry(rs.getString("school_country"));
                   sfcd.setSchoolZipcode(rs.getString("school_zipcode"));
                   sfcd.setStudentId(rs.getInt("student_id"));
                   sfcd.setStudentName(rs.getString("student_name"));
                   sfcd.setClassId(rs.getInt("class_id"));
                   sfcd.setClassName(rs.getString("class_name"));
                   sfcd.setSectionId(rs.getInt("section_id"));
                   sfcd.setSectionName(rs.getString("section_name"));
                   sfcd.setSessionId(rs.getInt("session_id"));
                   sfcd.setSessionName(rs.getString("academic_session"));
                   sfcd.setTotalFeeAssigned(rs.getDouble("total_fee_assigned"));
                   sfcd.setTotalDiscount(rs.getDouble("total_discount"));
                   sfcd.setGrossStudentFee(rs.getDouble("gross_student_fee"));
                   sfcd.setFeeDetails(Arrays.asList((String[])rs.getArray("fee_details").getArray()));
                   return sfcd;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return studentFeeCollectionDetails;
   }
}
