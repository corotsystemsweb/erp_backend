package com.sms.dao.impl;

import com.sms.dao.HostelDao;
import com.sms.model.*;
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
        String sql = "INSERT INTO hostel (name, type, total_rooms, total_capacity, no_of_floors, warden_name, contact_number, address, status, remarks) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, hostelDetails.getName());
                ps.setString(2, hostelDetails.getType());
                ps.setInt(3, hostelDetails.getTotalRooms());
                ps.setInt(4, hostelDetails.getTotalCapacity());
                ps.setInt(5, hostelDetails.getNoOfFloors() > 0 ? hostelDetails.getNoOfFloors() : 1);  // ✅ NEW
                ps.setString(6, hostelDetails.getWardenName());
                ps.setString(7, hostelDetails.getContactNumber());
                ps.setString(8, hostelDetails.getAddress());
                ps.setString(9, hostelDetails.getStatus() != null ? hostelDetails.getStatus() : "Active");
                ps.setString(10, hostelDetails.getRemarks());
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
        String sql = "SELECT hostel_id, name, type, total_rooms, total_capacity, no_of_floors, warden_name, contact_number, address, status, created_date, updated_date, remarks FROM hostel WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{hostelId}, (rs, rowNum) -> {
                HostelDetails hostel = new HostelDetails();
                hostel.setHostelId(rs.getInt("hostel_id"));
                hostel.setName(rs.getString("name"));
                hostel.setType(rs.getString("type"));
                hostel.setTotalRooms(rs.getInt("total_rooms"));
                hostel.setTotalCapacity(rs.getInt("total_capacity"));
                hostel.setNoOfFloors(rs.getInt("no_of_floors"));
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
        String sql = "SELECT hostel_id, name, type, total_rooms, total_capacity, no_of_floors, warden_name, contact_number, address, status, created_date, updated_date, remarks FROM hostel WHERE (deleted IS NULL OR deleted = false) ORDER BY hostel_id DESC";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.query(sql, (rs, rowNum) -> {
                HostelDetails hostel = new HostelDetails();
                hostel.setHostelId(rs.getInt("hostel_id"));
                hostel.setName(rs.getString("name"));
                hostel.setType(rs.getString("type"));
                hostel.setTotalRooms(rs.getInt("total_rooms"));
                hostel.setTotalCapacity(rs.getInt("total_capacity"));
                hostel.setNoOfFloors(rs.getInt("no_of_floors"));
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
        String sql = "UPDATE hostel SET name = ?, type = ?, total_rooms = ?, total_capacity = ?, no_of_floors = ?, warden_name = ?, contact_number = ?, address = ?, status = ?, remarks = ? WHERE hostel_id = ? AND (deleted IS NULL OR deleted = false)";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            int rowsAffected = jdbcTemplate.update(sql,
                    hostelDetails.getName(),
                    hostelDetails.getType(),
                    hostelDetails.getTotalRooms(),
                    hostelDetails.getTotalCapacity(),
                    hostelDetails.getNoOfFloors(),
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

}