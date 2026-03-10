package com.sms.dao.impl;

import com.sms.dao.BookCategoryDao;
import com.sms.model.BookCategroyDetails;
import com.sms.model.SubjectDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
public class BookCategoryDaoImpl implements BookCategoryDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public BookCategoryDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookCategroyDetails addBookCategory(BookCategroyDetails bookCategroyDetails, String schoolCode) throws Exception {
        String sql="insert into book_category(school_id, session_id, book_category_name, book_description, updated_by, update_date_time) values(?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,bookCategroyDetails.getSchoolId());
                ps.setInt(2,bookCategroyDetails.getSessionId());
                ps.setString(3,bookCategroyDetails.getBookCategoryName());
                ps.setString(4,bookCategroyDetails.getBookDescription());
                ps.setInt(5,bookCategroyDetails.getUpdatedBy());
                ps.setTimestamp(6,bookCategroyDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("book_category_id")){
                int generatedId=((Number) keys.get("book_category_id")).intValue();
                bookCategroyDetails.setBookCategoryId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookCategroyDetails ;
    }

    @Override
    public BookCategroyDetails getBookCategoryById(int bookCategoryId, String schoolCode) throws Exception {
        String sql = "SELECT book_category_id,book_category_name,book_description FROM book_category WHERE book_category_id=?";
        System.out.println("sql"+sql);
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        BookCategroyDetails bookCategroyDetails=null;
        try{
            bookCategroyDetails = jdbcTemplate.queryForObject(sql, new Object[]{bookCategoryId}, new RowMapper<BookCategroyDetails>() {
                @Override
                public BookCategroyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BookCategroyDetails bcd = new BookCategroyDetails();
                    bcd.setBookCategoryId(rs.getInt("book_category_id"));
                    bcd.setBookCategoryName(rs.getString("book_category_name"));
                    bcd.setBookDescription(rs.getString("book_description"));
                    return bcd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookCategroyDetails;
    }

    @Override
    public List<BookCategroyDetails> getBookCategory(String schoolCode) throws Exception {
        String sql = "SELECT book_category_id,book_category_name,book_description FROM book_category order by book_category_id asc ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<BookCategroyDetails> bookCategroyDetails=null;
        try{
            bookCategroyDetails = jdbcTemplate.query(sql, new RowMapper<BookCategroyDetails>() {
                @Override
                public BookCategroyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BookCategroyDetails bcd = new BookCategroyDetails();
                    bcd.setBookCategoryId(rs.getInt("book_category_id"));
                    bcd.setBookCategoryName(rs.getString("book_category_name"));
                    bcd.setBookDescription(rs.getString("book_description"));
                    return bcd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookCategroyDetails;
    }
    public boolean isDuplicateBook(BookCategroyDetails bookCategroyDetails, int bookCategoryId, String schoolCode) throws Exception {
        String checkSql = "SELECT COUNT(*) FROM book_category WHERE book_category_id != ? AND book_category_name = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, bookCategoryId, bookCategroyDetails.getBookCategoryName());
           return count != null && count>0;
        } catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            return false;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public BookCategroyDetails updateById(BookCategroyDetails bookCategroyDetails, int bookCategoryId, String schoolCode) throws Exception {
        if(isDuplicateBook(bookCategroyDetails,bookCategoryId,schoolCode)){
            return null;
        }
        String sql = "UPDATE book_category SET school_id = ?, session_id = ?, book_category_name = ?, book_description = ?, updated_by = ?, update_date_time = ? WHERE book_category_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    bookCategroyDetails.getSchoolId(),
                    bookCategroyDetails.getSessionId(),
                    bookCategroyDetails.getBookCategoryName(),
                    bookCategroyDetails.getBookDescription(),
                    bookCategroyDetails.getUpdatedBy(),
                    bookCategroyDetails.getUpdateDateTime(),
                    bookCategoryId);
            if (rowEffected > 0) {
                return bookCategroyDetails;
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
    public Boolean deleteBookCategory(int bookCategoryId, String schoolCode) throws Exception {
        String sql = "delete from book_category where book_category_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{bookCategoryId});
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

    @Override
    public List<BookCategroyDetails> getBookCategoryBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = " SELECT \n" +
                "    book_category_id,\n" +
                "    book_category_name,\n" +
                "    book_description \n" +
                "FROM \n" +
                "    book_category  \n" +
                "WHERE \n" +
                "    concat_ws(' ', book_category_id, book_category_name, book_description) ILIKE ? \n" +
                "ORDER BY \n" +
                "    book_category_id ASC;";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<BookCategroyDetails> bookCategroyDetails=null;
        try{
            bookCategroyDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"},  new RowMapper<BookCategroyDetails>() {
                @Override
                public BookCategroyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BookCategroyDetails bcd = new BookCategroyDetails();
                    bcd.setBookCategoryId(rs.getInt("book_category_id"));
                    bcd.setBookCategoryName(rs.getString("book_category_name"));
                    bcd.setBookDescription(rs.getString("book_description"));
                    return bcd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookCategroyDetails;
    }

}
