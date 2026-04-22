package com.sms.dao.impl;

import com.sms.dao.HostelDao;
import com.sms.model.HostelDetails;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.*;
import java.sql.Date;
import java.util.*;

@Repository
public class HostelDaoImpl implements HostelDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public HostelDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public HostelDetails addHostel(HostelDetails hostelDetails, String schoolCode) throws Exception {
        String sql = "INSERT INTO hostel (name, type, total_rooms, total_capacity, warden_name, contact_number, address, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, hostelDetails.getName());
                ps.setString(2, hostelDetails.getType());
                ps.setInt(3, hostelDetails.getTotalRooms());
                ps.setInt(4, hostelDetails.getTotalCapacity());
                ps.setString(5, hostelDetails.getWardenName());
                ps.setString(6, hostelDetails.getContactNumber());
                ps.setString(7, hostelDetails.getAddress());
                ps.setString(8, hostelDetails.getStatus() != null ? hostelDetails.getStatus() : "Active");
                ps.setString(9, hostelDetails.getRemarks());
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("hostel_id")) {
                int generatedId = ((Number) keys.get("hostel_id")).intValue();
                hostelDetails.setHostelId(generatedId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return hostelDetails;
    }

    @Override
    public HostelDetails getHostelById(String schoolCode, int hostelId) throws Exception {
        String sql = "SELECT hostel_id, name, type, total_rooms, total_capacity, warden_name, contact_number, address, status, created_date, updated_date, remarks FROM hostel WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{hostelId}, (rs, rowNum) -> {
                HostelDetails hostel = new HostelDetails();
                hostel.setHostelId(rs.getInt("hostel_id"));
                hostel.setName(rs.getString("name"));
                hostel.setType(rs.getString("type"));
                hostel.setTotalRooms(rs.getInt("total_rooms"));
                hostel.setTotalCapacity(rs.getInt("total_capacity"));
                hostel.setWardenName(rs.getString("warden_name"));
                hostel.setContactNumber(rs.getString("contact_number"));
                hostel.setAddress(rs.getString("address"));
                hostel.setStatus(rs.getString("status"));
                hostel.setCreatedDate(rs.getTimestamp("created_date"));
                hostel.setUpdatedDate(rs.getTimestamp("updated_date"));
                hostel.setRemarks(rs.getString("remarks"));
                return hostel;
            });
        } catch (Exception e) {
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<HostelDetails> getAllHostels(String schoolCode) throws Exception {
        String sql = "SELECT hostel_id, name, type, total_rooms, total_capacity, warden_name, contact_number, address, status, created_date, updated_date, remarks FROM hostel WHERE (deleted IS NULL OR deleted = false) ORDER BY hostel_id DESC";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                HostelDetails hostel = new HostelDetails();
                hostel.setHostelId(rs.getInt("hostel_id"));
                hostel.setName(rs.getString("name"));
                hostel.setType(rs.getString("type"));
                hostel.setTotalRooms(rs.getInt("total_rooms"));
                hostel.setTotalCapacity(rs.getInt("total_capacity"));
                hostel.setWardenName(rs.getString("warden_name"));
                hostel.setContactNumber(rs.getString("contact_number"));
                hostel.setAddress(rs.getString("address"));
                hostel.setStatus(rs.getString("status"));
                hostel.setCreatedDate(rs.getTimestamp("created_date"));
                hostel.setUpdatedDate(rs.getTimestamp("updated_date"));
                hostel.setRemarks(rs.getString("remarks"));
                return hostel;
            });
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public HostelDetails updateHostel(HostelDetails hostelDetails, String schoolCode) throws Exception {
        String sql = "UPDATE hostel SET name = ?, type = ?, total_rooms = ?, total_capacity = ?, warden_name = ?, contact_number = ?, address = ?, status = ?, remarks = ? WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                    hostelDetails.getName(),
                    hostelDetails.getType(),
                    hostelDetails.getTotalRooms(),
                    hostelDetails.getTotalCapacity(),
                    hostelDetails.getWardenName(),
                    hostelDetails.getContactNumber(),
                    hostelDetails.getAddress(),
                    hostelDetails.getStatus(),
                    hostelDetails.getRemarks(),
                    hostelDetails.getHostelId()
            );
            if (rowsAffected > 0) {
                return hostelDetails;
            }
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public boolean deleteHostel(String schoolCode, int hostelId) throws Exception {
        // Soft delete - just set deleted flag
        String sql = "UPDATE hostel SET deleted = true, updated_date = CURRENT_TIMESTAMP WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rowsAffected = jdbcTemplate.update(sql, hostelId);
            return rowsAffected > 0;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}