package com.sms.dao.impl;

import com.sms.dao.IssueBookDao;
import com.sms.model.AddBookDetails;
import com.sms.model.IssueBookDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Repository
public class IssueBookDaoImpl implements IssueBookDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public IssueBookDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public IssueBookDetails addIssueBookDetails(IssueBookDetails issueBookDetails, String schoolCode) throws Exception {
        String sql="insert into issue_book(school_id, session_id, class_id, section_id, student_id,book_id, issue_date, due_date, status, updated_by, update_date_time) values(?,?,?,?,?,?,?,?,?, ?, ?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,issueBookDetails.getSchoolId());
                ps.setInt(2,issueBookDetails.getSessionId());
                ps.setInt(3,issueBookDetails.getClassId());
                ps.setInt(4,issueBookDetails.getSectionId());
                ps.setInt(5,issueBookDetails.getStudentId());
                ps.setInt(6,issueBookDetails.getBookId());
                ps.setDate(7,issueBookDetails.getIssueDate());
                ps.setDate(8,issueBookDetails.getDueDate());
                ps.setString(9,issueBookDetails.getStatus());
                ps.setInt(10,issueBookDetails.getUpdatedBy());
                ps.setTimestamp(11,issueBookDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("issue_book_id")){
                int generatedId=((Number) keys.get("issue_book_id")).intValue();
                issueBookDetails.setIssueBookId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return issueBookDetails;
    }

    @Override
    public List<IssueBookDetails> getIssueBookDetails(String schoolCode) throws Exception {
        String sql = "SELECT " +
                "    ib.issue_book_id, " +
                "    spd.first_name || ' ' || spd.last_name AS student_name, " +
                "    anb.book_name, " +
                "    anb.isbn, " +
                "    anb.book_author_name AS book_author, " +
                "    ib.issue_date, " +
                "    ib.due_date, " +
                "    ib.status " +
                "FROM " +
                "    issue_book ib " +
                "JOIN " +
                "    student_personal_details spd ON ib.student_id = spd.student_id " +
                "JOIN " +
                "    add_new_book anb ON ib.book_id = anb.book_id " +
                "WHERE " +
                "    ib.deleted IS NOT TRUE";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<IssueBookDetails> issueBookDetails=null;
        try {
            issueBookDetails = jdbcTemplate.query(sql, new RowMapper<IssueBookDetails>() {
                @Override
                public IssueBookDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    IssueBookDetails ib = new IssueBookDetails();
                    ib.setIssueBookId(rs.getInt("issue_book_id"));
                    ib.setStudentName(rs.getString("student_name"));
                    ib.setBookName(rs.getString("book_name"));
                    ib.setIsbn(rs.getString("isbn"));
                    ib.setAuthorName(rs.getString("book_author"));
                    ib.setIssueDate(rs.getDate("issue_date"));
                    ib.setDueDate(rs.getDate("due_date"));
                    ib.setStatus(rs.getString("status"));
                    return ib;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return issueBookDetails;
    }

    @Override
    public IssueBookDetails getIssueBookDetailsById(int issueBookId, String schoolCode) throws Exception {
        String sql = "SELECT " +
                "    ib.issue_book_id, " +
                "    spd.first_name || ' ' || spd.last_name AS student_name, " +
                "    anb.book_name, " +
                "    anb.isbn, " +
                "    anb.book_author_name AS book_author, " +
                "    ib.issue_date, " +
                "    ib.due_date, " +
                "    ib.status " +
                "FROM " +
                "    issue_book ib " +
                "JOIN " +
                "    student_personal_details spd ON ib.student_id = spd.student_id " +
                "JOIN " +
                "    add_new_book anb ON ib.book_id = anb.book_id " +  // Added space here
                "WHERE " +
                "    issue_book_id = ? AND ib.deleted is not true";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        IssueBookDetails issueBookDetails=null;
        try{
            issueBookDetails = jdbcTemplate.queryForObject(sql, new Object[]{issueBookId},new RowMapper<IssueBookDetails>() {
                @Override
                public IssueBookDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    IssueBookDetails ib = new IssueBookDetails();
                    ib.setIssueBookId(rs.getInt("issue_book_id"));
                    ib.setStudentName(rs.getString("student_name"));
                    ib.setBookName(rs.getString("book_name"));
                    ib.setIsbn(rs.getString("isbn"));
                    ib.setAuthorName(rs.getString("book_author"));
                    ib.setIssueDate(rs.getDate("issue_date"));
                    ib.setDueDate(rs.getDate("due_date"));
                    ib.setStatus(rs.getString("status"));
                    return ib;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return issueBookDetails;
    }

    @Override
    public IssueBookDetails updateIssueBookDetails(IssueBookDetails issueBookDetails, int issueBookId, String schoolCode) throws Exception {
        String sql = "UPDATE issue_book SET  due_date = ?, status = ?, updated_by = ?, update_date_time = ? WHERE issue_book_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    issueBookDetails.getDueDate(),
                    issueBookDetails.getStatus(),
                    issueBookDetails.getUpdatedBy(),
                    issueBookDetails.getUpdateDateTime(),
                    issueBookId);
            if (rowEffected > 0) {
                return issueBookDetails;
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean issueBookSoftDelete(int issueBookId, String schoolCode) throws Exception {
        String sql = "UPDATE issue_book SET deleted = TRUE WHERE issue_book_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql,issueBookId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<IssueBookDetails> getIssueBookDetailsbBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = " SELECT \n" +
                "    ib.issue_book_id, \n" +
                "    spd.first_name || ' ' || spd.last_name AS student_name, \n" +
                "    anb.book_name, \n" +
                "    anb.isbn, \n" +
                "    anb.book_author_name AS book_author, \n" +
                "    ib.issue_date, \n" +
                "    ib.due_date, \n" +
                "    ib.status \n" +
                "FROM \n" +
                "    issue_book ib \n" +
                "JOIN \n" +
                "    student_personal_details spd \n" +
                "    ON ib.student_id = spd.student_id \n" +
                "JOIN \n" +
                "    add_new_book anb \n" +
                "    ON ib.book_id = anb.book_id \n" +
                "WHERE \n" +
                "    ib.deleted IS NOT TRUE \n" +
                "    AND concat_ws(' ', \n" +
                "        ib.issue_book_id, \n" +
                "        spd.first_name || ' ' || spd.last_name, \n" +
                "        anb.book_name, \n" +
                "        anb.isbn, \n" +
                "        anb.book_author_name, \n" +
                "        ib.issue_date, \n" +
                "        ib.due_date, \n" +
                "        ib.status\n" +
                "    ) ilike ?\n" +
                "ORDER BY \n" +
                "    ib.issue_book_id ASC";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<IssueBookDetails> issueBookDetails = null;
        try {
            issueBookDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<IssueBookDetails>() {
                @Override
                public IssueBookDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    IssueBookDetails ib = new IssueBookDetails();
                    ib.setIssueBookId(rs.getInt("issue_book_id"));
                    ib.setStudentName(rs.getString("student_name"));
                    ib.setBookName(rs.getString("book_name"));
                    ib.setIsbn(rs.getString("isbn"));
                    ib.setAuthorName(rs.getString("book_author"));
                    ib.setIssueDate(rs.getDate("issue_date"));
                    ib.setDueDate(rs.getDate("due_date"));
                    ib.setStatus(rs.getString("status"));
                    return ib;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return issueBookDetails;
    }
}
