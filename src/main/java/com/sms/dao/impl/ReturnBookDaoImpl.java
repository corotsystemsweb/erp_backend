package com.sms.dao.impl;
import java.util.List;
import com.sms.dao.ReturnBookDao;
import com.sms.model.ReturnBookDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Map;

@Repository
public class ReturnBookDaoImpl implements ReturnBookDao {

    @Override
    public ReturnBookDetails addReturnBook(ReturnBookDetails details, String schoolCode) throws Exception {

        String sql = "insert into return_book(school_id, session_id, class_id, section_id, student_id, book_id, issue_book_id, return_date, book_condition, fine_amount, remarks, updated_by, update_date_time) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

                ps.setInt(1, details.getSchoolId());
                ps.setInt(2, details.getSessionId());
                ps.setInt(3, details.getClassId());
                ps.setInt(4, details.getSectionId());
                ps.setInt(5, details.getStudentId());
                ps.setInt(6, details.getBookId());
                ps.setInt(7, details.getIssueBookId());
                ps.setDate(8, new java.sql.Date(details.getReturnDate().getTime()));
                ps.setString(9, details.getBookCondition());
                ps.setDouble(10, details.getFineAmount());
                ps.setString(11, details.getRemarks());
                ps.setInt(12, details.getUpdatedBy());
                ps.setTimestamp(13, details.getUpdateDateTime());

                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("return_book_id")) {
                int generatedId = ((Number) keys.get("return_book_id")).intValue();
                details.setReturnBookId(generatedId);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return details;


    }



@Override
public List<ReturnBookDetails> getReturnBookDetails(String schoolCode) throws Exception {

    String sql = "SELECT " +
            "rb.return_book_id, " +
            "rb.school_id, " +
            "rb.session_id, " +
            "rb.class_id, " +
            "rb.section_id, " +
            "rb.student_id, " +
            "rb.book_id, " +
            "rb.issue_book_id, " +
            "rb.updated_by, " +
            "spd.first_name || ' ' || spd.last_name AS student_name, " +
            "anb.book_name, " +
            "anb.isbn, " +
            "anb.book_author_name AS book_author, " +
            "rb.return_date, " +
            "rb.book_condition, " +
            "rb.fine_amount, " +
            "rb.remarks " +
            "FROM return_book rb " +
            "JOIN student_personal_details spd ON rb.student_id = spd.student_id " +
            "JOIN add_new_book anb ON rb.book_id = anb.book_id " +
            "WHERE rb.deleted IS NOT TRUE";

    JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

    List<ReturnBookDetails> list = null;

    try {
        list = jdbcTemplate.query(sql, (rs, rowNum) -> {
            ReturnBookDetails rb = new ReturnBookDetails();

            rb.setReturnBookId(rs.getInt("return_book_id"));
            rb.setSchoolId(rs.getInt("school_id"));
            rb.setSessionId(rs.getInt("session_id"));
            rb.setClassId(rs.getInt("class_id"));
            rb.setSectionId(rs.getInt("section_id"));
            rb.setStudentId(rs.getInt("student_id"));
            rb.setBookId(rs.getInt("book_id"));
            rb.setIssueBookId(rs.getInt("issue_book_id"));
            rb.setUpdatedBy(rs.getInt("updated_by"));
            rb.setStudentName(rs.getString("student_name"));
            rb.setBookName(rs.getString("book_name"));
            rb.setIsbn(rs.getString("isbn"));
            rb.setAuthorName(rs.getString("book_author"));
            rb.setReturnDate(rs.getDate("return_date"));
            rb.setBookCondition(rs.getString("book_condition"));
            rb.setFineAmount(rs.getDouble("fine_amount"));
            rb.setRemarks(rs.getString("remarks"));

            return rb;
        });

    } catch (Exception e) {
        e.printStackTrace();
        return null;
    } finally {
        DatabaseUtil.closeDataSource(jdbcTemplate);
    }

    return list;
}


    @Override
    public ReturnBookDetails updateReturnBook(ReturnBookDetails details, int returnBookId, String schoolCode) throws Exception {

        String sql = "UPDATE return_book SET " +
                "school_id = ?, " +
                "session_id = ?, " +
                "class_id = ?, " +
                "section_id = ?, " +
                "student_id = ?, " +
                "book_id = ?, " +
                "issue_book_id = ?, " +
                "return_date = ?, " +
                "book_condition = ?, " +
                "fine_amount = ?, " +
                "remarks = ?, " +
                "updated_by = ?, " +
                "update_date_time = ?, " +
                "deleted = ? " +
                "WHERE return_book_id = ?";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            int rows = jdbcTemplate.update(sql,
                    details.getSchoolId(),
                    details.getSessionId(),
                    details.getClassId(),
                    details.getSectionId(),
                    details.getStudentId(),
                    details.getBookId(),
                    details.getIssueBookId(),
                    new java.sql.Date(details.getReturnDate().getTime()),
                    details.getBookCondition(),
                    details.getFineAmount() != null ? details.getFineAmount() : 0,
                    details.getRemarks(),
                    details.getUpdatedBy(),
                    details.getUpdateDateTime(),
                    details.isDeleted(),
                    returnBookId
            );

            if (rows > 0) {
                return details;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }


}




