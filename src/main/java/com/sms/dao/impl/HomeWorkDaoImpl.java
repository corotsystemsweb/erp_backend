
package com.sms.dao.impl;

import com.sms.dao.HomeWorkDao;
import com.sms.model.HomeWorkDetails;
import com.sms.model.ImageDetails;
import com.sms.model.SchoolDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import com.sms.util.DateUtil;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@Repository
public class HomeWorkDaoImpl implements HomeWorkDao {
    @Value("${file.upload-dir}")
    private String FOLDER_PATH;
    private final JdbcTemplate jdbcTemplate;

    public HomeWorkDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    /*@Override
    public boolean addPDF(MultipartFile file, int hwId) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String pdfFileName = hwId + ".pdf";
        String pdfPath = FOLDER_PATH + File.separator + pdfFileName;
        byte[] pdfContent;
        if (isPDF(originalFileName)) {
            pdfContent = file.getBytes();
        } else if (isImage(originalFileName)) {
            pdfContent = convertImageToPDF(file.getBytes());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + originalFileName);
        }
        try (FileOutputStream fos = new FileOutputStream(pdfPath)) {
            fos.write(pdfContent);
        }

        return true;
    }

    private boolean isPDF(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }

    private boolean isImage(String fileName) {
        return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
    }
   //convert any file into pdf
    private byte[] convertImageToPDF(byte[] imageContent) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageContent);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDImageXObject imageXObject = JPEGFactory.createFromStream(document, inputStream);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(imageXObject, 0, 0);
            contentStream.close();
            document.save(outputStream);
            document.close();
            return outputStream.toByteArray();
        }
    }*/
    @Override
    public boolean addPDF(MultipartFile file, String schoolCode, int hwId) throws Exception {
        String originalFileName = file.getOriginalFilename();
        String pdfFileName = hwId + ".pdf";
        String pdfPath = FOLDER_PATH + File.separator + schoolCode + File.separator+ pdfFileName;
        // Ensure the directory exists
        File directory = new File(FOLDER_PATH + File.separator + schoolCode);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        byte[] pdfContent;
        if (isPDF(originalFileName)) {
            pdfContent = file.getBytes();
        } else if (isImage(originalFileName)) {
            pdfContent = convertImageToPDF(file.getBytes());
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + originalFileName);
        }
        try (FileOutputStream fos = new FileOutputStream(pdfPath)) {
            fos.write(pdfContent);
        }

        return true;
    }

    private boolean isPDF(String fileName) {
        return fileName.toLowerCase().endsWith(".pdf");
    }

    private boolean isImage(String fileName) {
        return fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg");
    }
    //convert any file into pdf
    private byte[] convertImageToPDF(byte[] imageContent) throws Exception {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(imageContent);
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);
            PDImageXObject imageXObject = JPEGFactory.createFromStream(document, inputStream);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.drawImage(imageXObject, 0, 0);
            contentStream.close();
            document.save(outputStream);
            document.close();
            return outputStream.toByteArray();
        }
    }
    @Override
    public HomeWorkDetails addHomeWork(HomeWorkDetails homeWorkDetails, String schoolCode) {

        String sql = "insert into home_work (school_id, session_id, class_id, section_id, subject_id, assign_home_work_date, description, updated_by, updated_date) values(?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, homeWorkDetails.getSchoolId());
               ps.setInt(2,homeWorkDetails.getSessionId());
               ps.setInt(3,homeWorkDetails.getClassId());
               ps.setInt(4,homeWorkDetails.getSectionId());
               ps.setInt(5,homeWorkDetails.getSubjectId());
               ps.setDate(6,homeWorkDetails.getAssignHomeWorkDate());
               ps.setString(7,homeWorkDetails.getDescription());
               ps.setInt(8,homeWorkDetails.getUpdatedBy());
               ps.setDate(9, java.sql.Date.valueOf(DateUtil.NOW_LOCAL_DATE()));
               return ps;
            }, keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("hw_id")){
                int generatedKey = ((Number) keys.get("hw_id")).intValue();
                homeWorkDetails.setHwId(generatedKey);
            }
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return homeWorkDetails;
    }

    /*@Override
    public byte[] getPDFBytes(int hwId) throws Exception {
        String fileName = hwId + ".pdf";
        String filePath = FOLDER_PATH + fileName;
        return Files.readAllBytes(Paths.get(filePath));
    }*/
    @Override
    public byte[] getPDFBytes(String schoolCode, int hwId) throws Exception {
        String fileName = hwId + ".pdf";
        String filePath = FOLDER_PATH + schoolCode + File.separator + fileName;
        return Files.readAllBytes(Paths.get(filePath));
    }
    @Override
    public List<HomeWorkDetails> getAssignHomeWork(String schoolCode) throws Exception {
        String sql = "SELECT\n" +
                "hw.hw_id,\n" +
                "    ses.academic_session,\n" +
                "    cls.class_name,\n" +
                "    sec.section_name,\n" +
                "    sub.subject_name,\n" +
                "    hw.description,\n" +
                "    hw.assign_home_work_date\n" +
                "FROM\n" +
                "    home_work hw\n" +
                "INNER JOIN\n" +
                "    session ses ON hw.session_id = ses.session_id\n" +
                "INNER JOIN\n" +
                "    mst_class cls ON hw.class_id = cls.class_id\n" +
                "INNER JOIN\n" +
                "    mst_section sec ON hw.section_id = sec.section_id\n" +
                "INNER JOIN\n" +
                "    mst_subject sub ON hw.subject_id = sub.subject_id";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<HomeWorkDetails> homeWorkDetails=null;
        try{
        homeWorkDetails = jdbcTemplate.query(sql, new RowMapper<HomeWorkDetails>() {
            @Override
            public HomeWorkDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeWorkDetails hw = new HomeWorkDetails();
                hw.setHwId(rs.getInt("hw_id"));
                hw.setAcademicSession(rs.getString("academic_session"));
                hw.setClassName(rs.getString("class_name"));
                hw.setSectionName(rs.getString("section_name"));
                hw.setSubjectName(rs.getString("subject_name"));
                hw.setDescription(rs.getString("description"));
                hw.setAssignHomeWorkDate(rs.getDate("assign_home_work_date"));
                return hw;
            }
        });
        return homeWorkDetails;
    }catch (Exception e) {
            e.printStackTrace();
        return null;
        }  finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public HomeWorkDetails getAssignHomeWorkById(int hwId, String schoolCode) throws Exception {
        String sql = "SELECT\n" +
                "    hw.hw_id,\n" +
                "    hw.session_id ,\n" +
                "    ses.academic_session,\n" +
                "    hw.class_id ,\n" +
                "    cls.class_name,\n" +
                "    hw.section_id ,\n" +
                "    sec.section_name,\n" +
                "    hw.subject_id ,\n" +
                "    sub.subject_name,\n" +
                "    hw.description,\n" +
                "    hw.assign_home_work_date\n" +
                "FROM\n" +
                "    home_work hw\n" +
                "INNER JOIN\n" +
                "    session ses ON hw.session_id = ses.session_id\n" +
                "INNER JOIN\n" +
                "    mst_class cls ON hw.class_id = cls.class_id\n" +
                "INNER JOIN\n" +
                "    mst_section sec ON hw.section_id = sec.section_id\n" +
                "INNER JOIN\n" +
                "    mst_subject sub ON hw.subject_id = sub.subject_id\n" +
                "WHERE hw.hw_id = ?\n";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        HomeWorkDetails homeWorkDetails=null;
        try{
        homeWorkDetails = jdbcTemplate.queryForObject(sql, new Object[]{hwId},new RowMapper<HomeWorkDetails>() {
            @Override
            public HomeWorkDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                HomeWorkDetails hw = new HomeWorkDetails();
                hw.setHwId(rs.getInt("hw_id"));
                hw.setSessionId(rs.getInt("session_id"));
                hw.setAcademicSession(rs.getString("academic_session"));
                hw.setClassId(rs.getInt("class_id"));
                hw.setClassName(rs.getString("class_name"));
                hw.setSectionId(rs.getInt("section_id"));
                hw.setSectionName(rs.getString("section_name"));
                hw.setSubjectId(rs.getInt("subject_id"));
                hw.setSubjectName(rs.getString("subject_name"));
                hw.setDescription(rs.getString("description"));
                hw.setAssignHomeWorkDate(rs.getDate("assign_home_work_date"));
                return hw;
            }
        });
        return homeWorkDetails;
    } catch (Exception e){
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public HomeWorkDetails updateAssignHomeWorkById(HomeWorkDetails homeWorkDetails, int hwId, String schoolCode) throws Exception {
        String sql = "update home_work set school_id = ?, session_id = ?, class_id = ?, section_id = ?, subject_id = ?, assign_home_work_date = ?, description = ?, updated_by = ?, updated_date = ? WHERE hw_id=?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        int rewAffected = jdbcTemplate.update(sql,
                homeWorkDetails.getSchoolId(),
                homeWorkDetails.getSessionId(),
                homeWorkDetails.getClassId(),
                homeWorkDetails.getSectionId(),
                homeWorkDetails.getSubjectId(),
                homeWorkDetails.getAssignHomeWorkDate(),
                homeWorkDetails.getDescription(),
                homeWorkDetails.getUpdatedBy(),
                homeWorkDetails.getUpdatedDate(),
        hwId);
        if(rewAffected > 0){
            return homeWorkDetails;
        }
        return null;
    }catch (Exception e){
            e.printStackTrace();
            return null;
   } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public boolean deleteAssignHomeWorkById(int hwId, String schoolCode) throws Exception {
        String sql = "delete from home_work where hw_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
        int rowAffected = jdbcTemplate.update(sql, new Object[]{hwId});
        if(rowAffected > 0 ){
            return true;
        }else{
            return false;
        }
    } catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        }
}




