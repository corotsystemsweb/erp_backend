package com.sms.dao.impl;

import com.sms.dao.BookStockDao;
import com.sms.model.AddBookDetails;
import com.sms.model.BookStockDetails;
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
public class BookStockDaoImpl implements BookStockDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public BookStockDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public BookStockDetails addBookStock(BookStockDetails bookStockDetails, String schoolCode) throws Exception {
        String sql="insert into book_stock(school_id, session_id, book_id, total_book_stock , updated_by, update_date_time) values(?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,bookStockDetails.getSchoolId());
                ps.setInt(2,bookStockDetails.getSessionId());
                ps.setInt(3,bookStockDetails.getBookId());
                ps.setString(4,bookStockDetails.getStockNumber());
                ps.setInt(5,bookStockDetails.getUpdatedBy());
                ps.setTimestamp(6,bookStockDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("book_stock_id")){
                int generatedId=((Number) keys.get("book_stock_id")).intValue();
                bookStockDetails.setBookStockId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookStockDetails;
    }

    @Override
    public List<BookStockDetails> getAllBookStock(String schoolCode) throws Exception {
        String sql = "SELECT " +
                "b.book_name, " +
                "c.book_category_name, " +
                "s.total_book_stock " +
                "FROM add_new_book b " +
                "JOIN book_category c ON b.book_category_id = c.book_category_id " +
                "JOIN book_stock s ON b.book_id = s.book_id " +
                "WHERE b.deleted IS NOT TRUE";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<BookStockDetails> bookStockDetails=null;
        try{
            bookStockDetails = jdbcTemplate.query(sql, new RowMapper<BookStockDetails>() {
                @Override
                public BookStockDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BookStockDetails bsc = new BookStockDetails();
                    bsc.setBookName(rs.getString("book_name"));
                    bsc.setBookCategoryName(rs.getString("book_category_name"));
                    bsc.setStockNumber(rs.getString("total_book_stock"));
                    return bsc;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookStockDetails;
    }

    @Override
    public List<BookStockDetails> getAllBookStockBySearchText(String searchText,String schoolCode) throws Exception {
        String sql = "SELECT \n" +
                "    b.book_name, \n" +
                "    c.book_category_name, \n" +
                "    s.total_book_stock \n" +
                "FROM \n" +
                "    add_new_book b \n" +
                "JOIN \n" +
                "    book_category c ON b.book_category_id = c.book_category_id \n" +
                "JOIN \n" +
                "    book_stock s ON b.book_id = s.book_id \n" +
                "where\n" +
                "concat_ws(' ', b.book_name, c.book_category_name,  s.total_book_stock ) ilike ? and b.deleted IS NOT true; \n" +
                "    \n";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<BookStockDetails> bookStockDetails=null;
        try{
            bookStockDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"},new RowMapper<BookStockDetails>() {
                @Override
                public BookStockDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    BookStockDetails bsc = new BookStockDetails();
                    bsc.setBookName(rs.getString("book_name"));
                    bsc.setBookCategoryName(rs.getString("book_category_name"));
                    bsc.setStockNumber(rs.getString("total_book_stock"));
                    return bsc;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return bookStockDetails;
    }
}
