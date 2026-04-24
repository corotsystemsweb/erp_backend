package com.sms.dao.impl;

import com.sms.dao.HostelDao;
import com.sms.model.*;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
        // ✅ Updated INSERT query with school_id, session_id, warden_staff_id
        String sql = "INSERT INTO hostel (school_id, session_id, name, type, total_rooms, total_capacity, no_of_floors, warden_staff_id, warden_name, contact_number, address, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            // Auto-populate warden_name from staff table if warden_staff_id is provided and warden_name is null
            String wardenName = hostelDetails.getWardenName();
            if (hostelDetails.getWardenStaffId() != null && (wardenName == null || wardenName.isEmpty())) {
                try {
                    String getStaffNameSql = "SELECT CONCAT(first_name, ' ', last_name) as staff_name FROM staff WHERE staff_id = ?";
                    wardenName = jdbcTemplate.queryForObject(getStaffNameSql, new Object[]{hostelDetails.getWardenStaffId()}, String.class);
                } catch (Exception e) {
                    // Staff not found, use null
                    wardenName = null;
                }
            }

            final String finalWardenName = wardenName;

            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, hostelDetails.getSchoolId());
                ps.setInt(2, hostelDetails.getSessionId());
                ps.setString(3, hostelDetails.getName());
                ps.setString(4, hostelDetails.getType());
                ps.setInt(5, hostelDetails.getTotalRooms());
                ps.setInt(6, hostelDetails.getTotalCapacity());
                ps.setInt(7, hostelDetails.getNoOfFloors() > 0 ? hostelDetails.getNoOfFloors() : 1);

                // Handle warden_staff_id (can be null)
                if (hostelDetails.getWardenStaffId() != null) {
                    ps.setInt(8, hostelDetails.getWardenStaffId());
                } else {
                    ps.setNull(8, Types.INTEGER);
                }

                ps.setString(9, finalWardenName);
                ps.setString(10, hostelDetails.getContactNumber());
                ps.setString(11, hostelDetails.getAddress());
                ps.setString(12, hostelDetails.getStatus() != null ? hostelDetails.getStatus() : "Active");
                ps.setString(13, hostelDetails.getRemarks());
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("hostel_id")) {
                int generatedId = ((Number) keys.get("hostel_id")).intValue();
                hostelDetails.setHostelId(generatedId);
            }

            return hostelDetails;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
    @Override
    public HostelDetails getHostelById(String schoolCode, int hostelId) throws Exception {
        // ✅ Updated SELECT query with new columns
        String sql = "SELECT hostel_id, school_id, session_id, name, type, total_rooms, total_capacity, no_of_floors, warden_staff_id, warden_name, contact_number, address, status, created_date, updated_date, remarks FROM hostel WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{hostelId}, (rs, rowNum) -> {
                HostelDetails hostel = new HostelDetails();
                hostel.setHostelId(rs.getInt("hostel_id"));
                hostel.setSchoolId(rs.getInt("school_id"));
                hostel.setSessionId(rs.getInt("session_id"));
                hostel.setName(rs.getString("name"));
                hostel.setType(rs.getString("type"));
                hostel.setTotalRooms(rs.getInt("total_rooms"));
                hostel.setTotalCapacity(rs.getInt("total_capacity"));
                hostel.setNoOfFloors(rs.getInt("no_of_floors"));

                int wardenStaffId = rs.getInt("warden_staff_id");
                if (!rs.wasNull()) {
                    hostel.setWardenStaffId(wardenStaffId);
                }

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
        // ✅ Updated SELECT query with new columns
        String sql = "SELECT hostel_id, school_id, session_id, name, type, total_rooms, total_capacity, no_of_floors, warden_staff_id, warden_name, contact_number, address, status, created_date, updated_date, remarks FROM hostel WHERE (deleted IS NULL OR deleted = false) ORDER BY hostel_id DESC";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                HostelDetails hostel = new HostelDetails();
                hostel.setHostelId(rs.getInt("hostel_id"));
                hostel.setSchoolId(rs.getInt("school_id"));
                hostel.setSessionId(rs.getInt("session_id"));
                hostel.setName(rs.getString("name"));
                hostel.setType(rs.getString("type"));
                hostel.setTotalRooms(rs.getInt("total_rooms"));
                hostel.setTotalCapacity(rs.getInt("total_capacity"));
                hostel.setNoOfFloors(rs.getInt("no_of_floors"));

                int wardenStaffId = rs.getInt("warden_staff_id");
                if (!rs.wasNull()) {
                    hostel.setWardenStaffId(wardenStaffId);
                }

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
        // ✅ Updated UPDATE query with new columns
        String sql = "UPDATE hostel SET school_id = ?, session_id = ?, name = ?, type = ?, total_rooms = ?, total_capacity = ?, no_of_floors = ?, warden_staff_id = ?, warden_name = ?, contact_number = ?, address = ?, status = ?, remarks = ? WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            // Auto-populate warden_name from staff table if warden_staff_id is provided
            String wardenName = hostelDetails.getWardenName();
            if (hostelDetails.getWardenStaffId() != null && (wardenName == null || wardenName.isEmpty())) {
                try {
                    String getStaffNameSql = "SELECT CONCAT(first_name, ' ', last_name) as staff_name FROM staff WHERE staff_id = ?";
                    wardenName = jdbcTemplate.queryForObject(getStaffNameSql, new Object[]{hostelDetails.getWardenStaffId()}, String.class);
                } catch (Exception e) {
                    wardenName = null;
                }
            }

            int rowsAffected = jdbcTemplate.update(sql,
                    hostelDetails.getSchoolId(),
                    hostelDetails.getSessionId(),
                    hostelDetails.getName(),
                    hostelDetails.getType(),
                    hostelDetails.getTotalRooms(),
                    hostelDetails.getTotalCapacity(),
                    hostelDetails.getNoOfFloors(),
                    hostelDetails.getWardenStaffId(),
                    wardenName,
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

    // Add Room with Beds
    @Override
    public RoomDetails addRoom(AddRoomRequest request, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        RoomDetails roomDetails = new RoomDetails();

        final int[] generatedRoomIdHolder = new int[1];

        try {
            // Step 1: VALIDATION - Check if room can be added
            String countSql = "SELECT COUNT(*) as room_count, (SELECT total_rooms FROM hostel WHERE hostel_id = ?) as max_rooms FROM hostel_rooms WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";
            Map<String, Object> countResult = jdbcTemplate.queryForMap(countSql, request.getHostelId(), request.getHostelId());

            int currentRoomCount = ((Number) countResult.get("room_count")).intValue();
            int maxRooms = ((Number) countResult.get("max_rooms")).intValue();

            // Check if maximum rooms limit reached
            if (currentRoomCount >= maxRooms) {
                throw new Exception("Cannot add room. Maximum room limit (" + maxRooms + ") reached for this hostel.");
            }

            // Step 2: Check if total beds after adding this room will exceed total_capacity
            int newBedsCount = request.getBeds() != null ? request.getBeds().size() : request.getCapacity();

            String bedCountSql = "SELECT COALESCE(SUM(total_beds), 0) as total_beds FROM hostel_rooms WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";
            Integer currentTotalBeds = jdbcTemplate.queryForObject(bedCountSql, new Object[]{request.getHostelId()}, Integer.class);
            currentTotalBeds = currentTotalBeds != null ? currentTotalBeds : 0;

            String capacitySql = "SELECT total_capacity FROM hostel WHERE hostel_id = ?";
            int maxCapacity = jdbcTemplate.queryForObject(capacitySql, new Object[]{request.getHostelId()}, Integer.class);

            if (currentTotalBeds + newBedsCount > maxCapacity) {
                throw new Exception("Cannot add room. Total beds (" + (currentTotalBeds + newBedsCount) + ") would exceed maximum capacity (" + maxCapacity + ") of this hostel.");
            }

            // Step 3: Insert into hostel_rooms table
            String roomSql = "INSERT INTO hostel_rooms (hostel_id, room_number, floor_number, room_type, capacity, rent_amount, remarks, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'Available')";

            KeyHolder roomKeyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(roomSql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, request.getHostelId());
                ps.setString(2, request.getRoomNumber());
                ps.setString(3, request.getFloorNumber() != null ? request.getFloorNumber() : "Ground Floor");
                ps.setString(4, request.getRoomType());
                ps.setInt(5, request.getCapacity());
                ps.setDouble(6, request.getRentAmount() != null ? request.getRentAmount() : 0);
                ps.setString(7, request.getRemarks());
                return ps;
            }, roomKeyHolder);

            Map<String, Object> roomKeys = roomKeyHolder.getKeys();
            int generatedRoomId = 0;
            if (roomKeys != null && roomKeys.containsKey("room_id")) {
                generatedRoomId = ((Number) roomKeys.get("room_id")).intValue();
                generatedRoomIdHolder[0] = generatedRoomId;
                roomDetails.setRoomId(generatedRoomId);
            }

            // Step 4: Get hostel name for response
            String hostelNameSql = "SELECT name FROM hostel WHERE hostel_id = ?";
            String hostelName = jdbcTemplate.queryForObject(hostelNameSql, new Object[]{request.getHostelId()}, String.class);

            // Step 5: Insert beds into hostel_beds table
            List<BedDetails> bedDetailsList = new ArrayList<>();
            if (request.getBeds() != null && !request.getBeds().isEmpty()) {
                String bedSql = "INSERT INTO hostel_beds (room_id, bed_number, bed_label, status) VALUES (?, ?, ?, 'Available')";

                final int finalRoomId = generatedRoomIdHolder[0];

                for (BedRequest bedRequest : request.getBeds()) {
                    KeyHolder bedKeyHolder = new GeneratedKeyHolder();
                    String bedNumber = request.getRoomNumber() + "-" + bedRequest.getBedLabel();

                    jdbcTemplate.update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(bedSql, Statement.RETURN_GENERATED_KEYS);
                        ps.setInt(1, finalRoomId);
                        ps.setString(2, bedNumber);
                        ps.setString(3, bedRequest.getBedLabel());
                        return ps;
                    }, bedKeyHolder);

                    Map<String, Object> bedKeys = bedKeyHolder.getKeys();
                    BedDetails bedDetails = new BedDetails();
                    if (bedKeys != null && bedKeys.containsKey("bed_id")) {
                        bedDetails.setBedId(((Number) bedKeys.get("bed_id")).intValue());
                    }
                    bedDetails.setRoomId(finalRoomId);
                    bedDetails.setBedNumber(bedNumber);
                    bedDetails.setBedLabel(bedRequest.getBedLabel());
                    bedDetails.setStatus("Available");
                    bedDetailsList.add(bedDetails);
                }
            }

            // Step 6: Update total_beds in hostel_rooms
            String updateBedsSql = "UPDATE hostel_rooms SET total_beds = ? WHERE room_id = ?";
            jdbcTemplate.update(updateBedsSql, bedDetailsList.size(), generatedRoomIdHolder[0]);

            // Step 7: Build response (DO NOT update hostel table - it only stores limits)
            roomDetails.setHostelId(request.getHostelId());
            roomDetails.setHostelName(hostelName);
            roomDetails.setRoomNumber(request.getRoomNumber());
            roomDetails.setFloorNumber(request.getFloorNumber() != null ? request.getFloorNumber() : "Ground Floor");
            roomDetails.setRoomType(request.getRoomType());
            roomDetails.setCapacity(request.getCapacity());
            roomDetails.setCurrentOccupancy(0);
            roomDetails.setTotalBeds(bedDetailsList.size());
            roomDetails.setStatus("Available");
            roomDetails.setRentAmount(request.getRentAmount() != null ? request.getRentAmount() : 0);
            roomDetails.setActive(true);
            roomDetails.setRemarks(request.getRemarks());
            roomDetails.setBeds(bedDetailsList);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;  // Throw the exception to controller for proper error response
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }

        return roomDetails;
    }

    //  Get Rooms by Hostel ID
    @Override
    public List<RoomDetails> getRoomsByHostel(String schoolCode, int hostelId) throws Exception {
        String sql = "SELECT hr.room_id, hr.hostel_id, h.name as hostel_name, hr.room_number, hr.floor_number, hr.room_type, hr.capacity, hr.current_occupancy, hr.total_beds, hr.status, hr.rent_amount, hr.is_active, hr.remarks, hr.created_date, hr.updated_date FROM hostel_rooms hr JOIN hostel h ON hr.hostel_id = h.hostel_id WHERE hr.hostel_id = ? AND (hr.deleted IS NULL OR hr.deleted = false) ORDER BY hr.room_number";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, new Object[]{hostelId}, (rs, rowNum) -> {
                RoomDetails room = new RoomDetails();
                room.setRoomId(rs.getInt("room_id"));
                room.setHostelId(rs.getInt("hostel_id"));
                room.setHostelName(rs.getString("hostel_name"));
                room.setRoomNumber(rs.getString("room_number"));
                room.setFloorNumber(rs.getString("floor_number"));
                room.setRoomType(rs.getString("room_type"));
                room.setCapacity(rs.getInt("capacity"));
                room.setCurrentOccupancy(rs.getInt("current_occupancy"));
                room.setTotalBeds(rs.getInt("total_beds"));
                room.setStatus(rs.getString("status"));
                room.setRentAmount(rs.getDouble("rent_amount"));
                room.setActive(rs.getBoolean("is_active"));
                room.setRemarks(rs.getString("remarks"));
                room.setCreatedDate(rs.getTimestamp("created_date"));
                room.setUpdatedDate(rs.getTimestamp("updated_date"));
                return room;
            });
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    //get hostel capacity status
    @Override
    public HostelCapacityStatus getHostelCapacityStatus(String schoolCode, int hostelId) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        try {
            String sql = "SELECT " +
                    "h.hostel_id, " +
                    "h.name as hostel_name, " +
                    "h.total_rooms as total_rooms_allowed, " +
                    "h.total_capacity as total_capacity_allowed, " +
                    "COALESCE(COUNT(DISTINCT hr.room_id), 0) as rooms_added, " +
                    "COALESCE(SUM(hr.total_beds), 0) as beds_added " +
                    "FROM hostel h " +
                    "LEFT JOIN hostel_rooms hr ON h.hostel_id = hr.hostel_id AND (hr.deleted IS NULL OR hr.deleted = false) " +
                    "WHERE h.hostel_id = ? AND (h.deleted IS NULL OR h.deleted = false) " +
                    "GROUP BY h.hostel_id, h.name, h.total_rooms, h.total_capacity";

            Map<String, Object> result = jdbcTemplate.queryForMap(sql, hostelId);

            HostelCapacityStatus status = new HostelCapacityStatus();
            status.setHostelId((Integer) result.get("hostel_id"));
            status.setHostelName((String) result.get("hostel_name"));

            int totalRoomsAllowed = ((Number) result.get("total_rooms_allowed")).intValue();
            int roomsAdded = ((Number) result.get("rooms_added")).intValue();
            status.setTotalRoomsAllowed(totalRoomsAllowed);
            status.setRoomsAdded(roomsAdded);
            status.setAvailableRooms(totalRoomsAllowed - roomsAdded);

            int totalCapacityAllowed = ((Number) result.get("total_capacity_allowed")).intValue();
            int bedsAdded = ((Number) result.get("beds_added")).intValue();
            status.setTotalCapacityAllowed(totalCapacityAllowed);
            status.setBedsAdded(bedsAdded);
            status.setAvailableCapacity(totalCapacityAllowed - bedsAdded);

            // Calculate percentages
            status.setRoomsFilledPercentage(totalRoomsAllowed > 0 ? (roomsAdded * 100.0 / totalRoomsAllowed) : 0);
            status.setCapacityFilledPercentage(totalCapacityAllowed > 0 ? (bedsAdded * 100.0 / totalCapacityAllowed) : 0);

            return status;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public HostelFeesDetails addHostelFees(AddHostelFeesRequest request, String schoolCode) throws Exception {
        // UPSERT with school_id and session_id
        String sql = "INSERT INTO hostel_fees (school_id, session_id, hostel_id, room_type, monthly_fee, security_deposit, admission_fee) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?) " +
                "ON CONFLICT (school_id, session_id, hostel_id, room_type) DO UPDATE SET " +
                "monthly_fee = EXCLUDED.monthly_fee, " +
                "security_deposit = EXCLUDED.security_deposit, " +
                "admission_fee = EXCLUDED.admission_fee, " +
                "updated_date = CURRENT_TIMESTAMP " +
                "RETURNING fee_id, created_date, updated_date";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            HostelFeesDetails result = new HostelFeesDetails();

            jdbcTemplate.queryForObject(sql, new Object[]{
                    request.getSchoolId(),
                    request.getSessionId(),
                    request.getHostelId(),
                    request.getRoomType(),
                    request.getMonthlyFee(),
                    request.getSecurityDeposit(),
                    request.getAdmissionFee()
            }, (rs, rowNum) -> {
                result.setFeeId(rs.getInt("fee_id"));
                result.setCreatedDate(rs.getTimestamp("created_date"));
                result.setUpdatedDate(rs.getTimestamp("updated_date"));
                return result;
            });

            // Get hostel name for response
            String hostelNameSql = "SELECT name FROM hostel WHERE hostel_id = ?";
            String hostelName = jdbcTemplate.queryForObject(hostelNameSql, new Object[]{request.getHostelId()}, String.class);

            result.setSchoolId(request.getSchoolId());
            result.setSessionId(request.getSessionId());
            result.setHostelId(request.getHostelId());
            result.setHostelName(hostelName);
            result.setRoomType(request.getRoomType());
            result.setMonthlyFee(request.getMonthlyFee());
            result.setSecurityDeposit(request.getSecurityDeposit());
            result.setAdmissionFee(request.getAdmissionFee());

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<HostelFeesDetails> getHostelFeesByHostel(String schoolCode, int hostelId) throws Exception {
        String sql = "SELECT hf.fee_id, hf.school_id, hf.session_id, hf.hostel_id, h.name as hostel_name, hf.room_type, " +
                "hf.monthly_fee, hf.security_deposit, hf.admission_fee, " +
                "hf.created_date, hf.updated_date " +
                "FROM hostel_fees hf " +
                "JOIN hostel h ON hf.hostel_id = h.hostel_id " +
                "WHERE hf.hostel_id = ? AND (hf.deleted IS NULL OR hf.deleted = false) " +
                "ORDER BY FIELD(hf.room_type, 'Single', 'Double', 'Triple', 'Quad')";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, new Object[]{hostelId}, (rs, rowNum) -> {
                HostelFeesDetails fees = new HostelFeesDetails();
                fees.setFeeId(rs.getInt("fee_id"));
                fees.setSchoolId(rs.getInt("school_id"));
                fees.setSessionId(rs.getInt("session_id"));
                fees.setHostelId(rs.getInt("hostel_id"));
                fees.setHostelName(rs.getString("hostel_name"));
                fees.setRoomType(rs.getString("room_type"));
                fees.setMonthlyFee(rs.getDouble("monthly_fee"));
                fees.setSecurityDeposit(rs.getDouble("security_deposit"));
                fees.setAdmissionFee(rs.getDouble("admission_fee"));
                fees.setCreatedDate(rs.getTimestamp("created_date"));
                fees.setUpdatedDate(rs.getTimestamp("updated_date"));
                return fees;
            });
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public HostelFeesDetails getHostelFeesByHostelAndRoomType(String schoolCode, int hostelId, String roomType) throws Exception {
        String sql = "SELECT hf.fee_id, hf.school_id, hf.session_id, hf.hostel_id, h.name as hostel_name, hf.room_type, " +
                "hf.monthly_fee, hf.security_deposit, hf.admission_fee, " +
                "hf.created_date, hf.updated_date " +
                "FROM hostel_fees hf " +
                "JOIN hostel h ON hf.hostel_id = h.hostel_id " +
                "WHERE hf.hostel_id = ? AND hf.room_type = ? AND (hf.deleted IS NULL OR hf.deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{hostelId, roomType}, (rs, rowNum) -> {
                HostelFeesDetails fees = new HostelFeesDetails();
                fees.setFeeId(rs.getInt("fee_id"));
                fees.setSchoolId(rs.getInt("school_id"));
                fees.setSessionId(rs.getInt("session_id"));
                fees.setHostelId(rs.getInt("hostel_id"));
                fees.setHostelName(rs.getString("hostel_name"));
                fees.setRoomType(rs.getString("room_type"));
                fees.setMonthlyFee(rs.getDouble("monthly_fee"));
                fees.setSecurityDeposit(rs.getDouble("security_deposit"));
                fees.setAdmissionFee(rs.getDouble("admission_fee"));
                fees.setCreatedDate(rs.getTimestamp("created_date"));
                fees.setUpdatedDate(rs.getTimestamp("updated_date"));
                return fees;
            });
        } catch (EmptyResultDataAccessException e) {
            return null;
        } finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

}