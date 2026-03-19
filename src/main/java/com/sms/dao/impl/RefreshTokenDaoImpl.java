package com.sms.dao.impl;

import com.sms.dao.RefreshTokenDao;
import com.sms.model.RefreshToken;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.time.Instant;

@Repository
public class RefreshTokenDaoImpl implements RefreshTokenDao {
    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RefreshToken findByUserName(String userName) {
        String sql = "SELECT * FROM refresh_token WHERE email = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{userName}, (rs, rowNum) -> {
                RefreshToken refreshToken = new RefreshToken();
                refreshToken.setTokenId(rs.getInt("token_id"));
                refreshToken.setUserName(rs.getString("email"));
                refreshToken.setRefreshToken(rs.getString("refresh_token"));
                //refreshToken.setExpiryTime(Instant.parse(rs.getString("expiry_time")));
                refreshToken.setExpiryTime(rs.getTimestamp("expiry_time").toInstant());
                return refreshToken;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
    @Override
    public RefreshToken findByRefreshToken(String refreshToken) {
        String sql = "SELECT * FROM refresh_token WHERE refresh_token = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{refreshToken}, (rs, rowNum) -> {
                RefreshToken refreshToken1 = new RefreshToken();
                refreshToken1.setTokenId(rs.getInt("token_id"));
                refreshToken1.setUserName(rs.getString("email"));
                refreshToken1.setRefreshToken(rs.getString("refresh_token"));
                //refreshToken1.setExpiryTime(Instant.parse(rs.getString("expiry_time")));
                refreshToken1.setExpiryTime(rs.getTimestamp("expiry_time").toInstant());
                return refreshToken1;
            });
        } catch (EmptyResultDataAccessException e) {
            return null; // Return null if no record is found
        }
    }

    /*@Override
    public void save(RefreshToken refreshToken) {
        String sql = "INSERT INTO refresh_token (email, refresh_token, expiry_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql,
                refreshToken.getUserName(), refreshToken.getRefreshToken(), refreshToken.getExpiryTime().toString());
    }*/
    @Override
    public void save(RefreshToken refreshToken) {
        String sql = "INSERT INTO refresh_token (email, refresh_token, expiry_time) VALUES (?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, refreshToken.getUserName());
            ps.setString(2, refreshToken.getRefreshToken());
            ps.setTimestamp(3, java.sql.Timestamp.from(refreshToken.getExpiryTime())); // Convert Instant to Timestamp
            return ps;
        });
    }

    @Override
    public void updateExpiryTime(String userName, Instant newExpiryTime) {
        String sql = "UPDATE refresh_token SET expiry_time = ? WHERE email = ?";
        //jdbcTemplate.update(sql, newExpiryTime.toString(), userName);
        jdbcTemplate.update(sql, java.sql.Timestamp.from(newExpiryTime), userName);
    }

    @Override
    public void deleteByRefreshToken(String refreshToken) {
        String sql = "DELETE FROM refresh_token WHERE refresh_token = ?";
        jdbcTemplate.update(sql, refreshToken);
    }
}
