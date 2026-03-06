package com.sms.dao.impl;

import com.sms.dao.AddBookDao;
import com.sms.model.AddBookDetails;
import com.sms.model.BookCategroyDetails;
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
public class AddBookDaoImpl implements AddBookDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AddBookDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public AddBookDetails addNewBookDetails(AddBookDetails addBookDetails, String schoolCode) throws Exception {
        String sql="insert into add_new_book(school_id, session_id, book_name, book_author_name, book_category_id, isbn, price, updated_by, update_date_time) values(?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder=new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps=connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                ps.setInt(1,addBookDetails.getSchoolId());
                ps.setInt(2,addBookDetails.getSessionId());
                ps.setString(3,addBookDetails.getBookName());
                ps.setString(4,addBookDetails.getBookAuthorName());
                ps.setInt(5,addBookDetails.getBookCategoryId());
                ps.setString(6,addBookDetails.getIsbn());
                ps.setString(7,addBookDetails.getPrice());
                ps.setInt(8,addBookDetails.getUpdatedBy());
                ps.setTimestamp(9,addBookDetails.getUpdateDateTime());
                return ps;
            },keyHolder);
            Map<String,Object> keys=keyHolder.getKeys();
            if(keys != null && keys.containsKey("book_id")){
                int generatedId=((Number) keys.get("book_id")).intValue();
                addBookDetails.setBookId(generatedId);
            }
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addBookDetails;
    }

    @Override
    public AddBookDetails getNewBookById(int bookId, String schoolCode) throws Exception {
        String sql = "SELECT b.book_id, b.book_name, b.book_author_name, b.book_category_id, bc.book_category_name, b.isbn, b.price " +
                "FROM add_new_book b " +
                "JOIN book_category bc ON b.book_category_id = bc.book_category_id " +
                "WHERE deleted IS NOT true and b.book_id = ? " +
                "ORDER BY b.book_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        AddBookDetails addBookDetails=null;
        try{
            addBookDetails = jdbcTemplate.queryForObject(sql, new Object[]{bookId}, new RowMapper<AddBookDetails>() {
                @Override
                public AddBookDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddBookDetails nb = new AddBookDetails();
                    nb.setBookId(rs.getInt("book_id"));
                    nb.setBookName(rs.getString("book_name"));
                    nb.setBookAuthorName(rs.getString("book_author_name"));
                    nb.setBookCategoryId(rs.getInt("book_category_id"));
                    nb.setBookCategory(rs.getString("book_category_name"));
                    nb.setIsbn(rs.getString("isbn"));
                    nb.setPrice(rs.getString("price"));
                    return nb;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addBookDetails;
    }

    @Override
    public List<AddBookDetails> getAllBook(String schoolCode) throws Exception {
        String sql = "SELECT b.book_id, b.book_name, b.book_author_name, b.book_category_id, bc.book_category_name, b.isbn, b.price " +
                "FROM add_new_book b " +
                "JOIN book_category bc ON b.book_category_id = bc.book_category_id " +
                "WHERE deleted IS not true " +
                "ORDER BY b.book_id ASC";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<AddBookDetails> addBookDetails=null;
       try{
           addBookDetails = jdbcTemplate.query(sql, new RowMapper<AddBookDetails>() {
               @Override
               public AddBookDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   AddBookDetails nb = new AddBookDetails();
                   nb.setBookId(rs.getInt("book_id"));
                   nb.setBookName(rs.getString("book_name"));
                   nb.setBookAuthorName(rs.getString("book_author_name"));
                   nb.setBookCategoryId(rs.getInt("book_category_id"));
                   nb.setBookCategory(rs.getString("book_category_name"));
                   nb.setIsbn(rs.getString("isbn"));
                   nb.setPrice(rs.getString("price"));
                   return nb;
               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
        return addBookDetails;
    }

    @Override
    public AddBookDetails updateById(AddBookDetails addBookDetails, int bookId, String schoolCode) throws Exception {
        String sql = "UPDATE add_new_book SET school_id = ?, session_id = ?, book_name = ?, book_author_name = ?, book_category_id = ?, isbn = ?, price = ?, updated_by = ?, update_date_time = ? WHERE book_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowEffected = jdbcTemplate.update(sql,
                    addBookDetails.getSchoolId(),
                    addBookDetails.getSessionId(),
                    addBookDetails.getBookName(),
                    addBookDetails.getBookAuthorName(),
                    addBookDetails.getBookCategoryId(),
                    addBookDetails.getIsbn(),
                    addBookDetails.getPrice(),
                    addBookDetails.getUpdatedBy(),
                    addBookDetails.getUpdateDateTime(),
                    bookId);
            if (rowEffected > 0) {
                return addBookDetails;
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
    public boolean softDeleteBook(int bookId, String schoolCode) throws Exception {
        String sql = "UPDATE add_new_book SET deleted = TRUE WHERE book_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int update = jdbcTemplate.update(sql, bookId);
            return update > 0;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<AddBookDetails> getAllBookBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "SELECT \n" +
                "    b.book_id, \n" +
                "    b.book_name, \n" +
                "    b.book_author_name, \n" +
                "    bc.book_category_name, \n" +
                "    b.isbn, \n" +
                "    b.price\n" +
                "FROM \n" +
                "    add_new_book b\n" +
                "JOIN \n" +
                "    book_category bc \n" +
                "ON \n" +
                "    b.book_category_id = bc.book_category_id\n" +
                "WHERE  \n" +
                "    concat_ws(' ', b.book_id, \n" +
                "    b.book_name, \n" +
                "    b.book_author_name, \n" +
                "    bc.book_category_name, \n" +
                "    b.isbn, \n" +
                "    b.price) ilike ? \n" +
                "    AND b.deleted IS NOT TRUE \n" +
                "ORDER BY \n" +
                "    b.book_id ASC\n";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddBookDetails> addBookDetails = null;
        try {
            addBookDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<AddBookDetails>() {
                @Override
                public AddBookDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddBookDetails nb = new AddBookDetails();
                    nb.setBookId(rs.getInt("book_id"));
                    nb.setBookName(rs.getString("book_name"));
                    nb.setBookAuthorName(rs.getString("book_author_name"));
                    nb.setBookCategory(rs.getString("book_category_name"));
                    nb.setIsbn(rs.getString("isbn"));
                    nb.setPrice(rs.getString("price"));
                    return nb;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addBookDetails;
    }
}
