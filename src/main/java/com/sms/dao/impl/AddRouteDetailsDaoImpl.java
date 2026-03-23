package com.sms.dao.impl;

import com.sms.dao.AddRouteDetailsDao;
import com.sms.model.AddRouteDetails;
import com.sms.model.RouteGroupDto;
import com.sms.model.RouteStopDTO;
import com.sms.util.DatabaseUtil;
import com.sms.util.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Repository
public class AddRouteDetailsDaoImpl implements AddRouteDetailsDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public AddRouteDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final Logger logger = LoggerFactory.getLogger(AddRouteDetailsDaoImpl.class);

    @Override
    public String addBulkRoute(List<AddRouteDetails> addRouteDetailsList, String schoolCode) throws Exception {
        if(addRouteDetailsList == null || addRouteDetailsList.isEmpty()){
            return "No route details are provided for insertion.";
        }
        String sql = "insert into add_route (school_id, vehicle_id, boarding_point, destination, max_fee, route_name, route_code, total_distance, estimated_time, stop_name, stop_fare, start_date, end_date, hash_value) values(?,?,?,?,?,?,?,?,?,?,?,CURRENT_DATE,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int[] batchResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AddRouteDetails ard = addRouteDetailsList.get(i);
                    String hashValue = HashUtil.generateRouteHash(ard.getBoardingPoint(), ard.getDestination());
                    ps.setInt(1, 1);
                    ps.setInt(2, ard.getVehicleId());
                    ps.setString(3, ard.getBoardingPoint().trim());
                    ps.setString(4, ard.getDestination().trim());
                    ps.setBigDecimal(5, ard.getMaxFee());

                    ps.setString(6, ard.getRouteName());
                    ps.setString(7, ard.getRouteCode());
                    ps.setObject(8, ard.getTotalDistance());
                    ps.setObject(9, ard.getEstimatedTime());

                    ps.setString(10, ard.getStopName());
                    ps.setBigDecimal(11, ard.getStopFare());

                    ps.setDate(12, Date.valueOf("9999-12-31"));
                    ps.setString(13, hashValue);
                }

                @Override
                public int getBatchSize() {
                    return addRouteDetailsList.size();
                }
            });
            // Check if all records are inserted successfully
            if(Arrays.stream(batchResult).allMatch(result -> result >= 0)){
                return "Add route details are added successfully";
            } else {
                throw new RuntimeException("Error occurred while inserting records!");
            }
        }catch(Exception e){
            logger.error("Database error while inserting add route for the school code: {}", schoolCode, e);
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public String addBulkRouteWithoutAcceptingHashValueInUpdate(List<AddRouteDetails> addRouteDetailsList, String schoolCode) throws Exception {
        if(addRouteDetailsList == null || addRouteDetailsList.isEmpty()){
            return "No route details are provided for insertion.";
        }
       // String sql = "insert into add_route (school_id, vehicle_id, boarding_point, destination, max_fee, stop_name, stop_fare, start_date, end_date, hash_value) values(?,?,?,?,?,?,?,CURRENT_DATE,?,?)";
        String sql = "insert into add_route (school_id, vehicle_id, boarding_point, destination, max_fee, route_name, route_code, total_distance, estimated_time, stop_name, stop_fare, start_date, end_date, hash_value) values(?,?,?,?,?,?,?,?,?,?,?,CURRENT_DATE,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int[] batchResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AddRouteDetails ard = addRouteDetailsList.get(i);
                    ps.setInt(1, 1);
                    ps.setInt(2, ard.getVehicleId());
                    ps.setString(3, ard.getBoardingPoint().trim());
                    ps.setString(4, ard.getDestination().trim());
                    ps.setBigDecimal(5, ard.getMaxFee());

                    ps.setString(6, ard.getRouteName());
                    ps.setString(7, ard.getRouteCode());
                    ps.setObject(8, ard.getTotalDistance());
                    ps.setObject(9, ard.getEstimatedTime());

                    ps.setString(10, ard.getStopName());
                    ps.setBigDecimal(11, ard.getStopFare());

                    ps.setDate(12, Date.valueOf("9999-12-31"));
                    ps.setString(13, ard.getHashValue());
                }

                @Override
                public int getBatchSize() {
                    return addRouteDetailsList.size();
                }
            });
            // Check if all records are inserted successfully
            if(Arrays.stream(batchResult).allMatch(result -> result >= 0)){
                return "Add route details are added successfully";
            } else {
                throw new RuntimeException("Error occurred while inserting records!");
            }
        }catch(Exception e){
            logger.error("Database error while inserting add route for the school code: {}", schoolCode, e);
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<AddRouteDetails> getAllRouteDetails(String schoolCode) throws Exception {
        String sql = "SELECT DISTINCT ar.route_id, ar.school_id, ar.vehicle_id, av.vehicle_number, ar.boarding_point, ar.destination, ar.max_fee,ar.route_name, ar.route_code, ar.total_distance, ar.estimated_time, ar.stop_name, ar.stop_fare, ar.start_date, ar.end_date, ar.hash_value FROM add_route ar JOIN add_vehicle av ON ar.vehicle_id = av.vehicle_id WHERE ar.end_date >= CURRENT_DATE order by route_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddRouteDetails> addRouteDetails = null;
        try{
            addRouteDetails = jdbcTemplate.query(sql, new RowMapper<AddRouteDetails>() {
                @Override
                public AddRouteDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddRouteDetails ard = new AddRouteDetails();
                    ard.setRouteId(rs.getInt("route_id"));
                    ard.setSchoolId(rs.getInt("school_id"));
                    ard.setVehicleId(rs.getInt("vehicle_id"));
                    ard.setVehicleNumber(rs.getString("vehicle_number"));
                    ard.setBoardingPoint(rs.getString("boarding_point"));
                    ard.setDestination(rs.getString("destination"));
                    ard.setMaxFee(rs.getBigDecimal("max_fee"));
                    ard.setRouteName(rs.getString("route_name"));
                    ard.setRouteCode(rs.getString("route_code"));
                    ard.setTotalDistance((Double) rs.getObject("total_distance"));
                    ard.setEstimatedTime((Integer) rs.getObject("estimated_time"));
                    ard.setStopName(rs.getString("stop_name"));
                    ard.setStopFare(rs.getBigDecimal("stop_fare"));
                    ard.setStartDate(rs.getDate("start_date"));
                    ard.setEndDate(rs.getDate("end_date"));
                    ard.setHashValue(rs.getString("hash_value"));
                    return ard;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addRouteDetails;
    }
    @Override
    public String deleteRouteDetails(List<Integer> routeIds, String schoolCode) throws Exception {
        if(routeIds == null || routeIds.isEmpty()){
            logger.warn("Route IDs list is null or empty for school code: {}", schoolCode);
            return "No records to delete";
        }
        String sql = "delete from add_route where route_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            logger.info("Deleting {} route details for school code: {}", routeIds.size(), schoolCode);
            int[] batchResult = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, routeIds.get(i));
                }

                @Override
                public int getBatchSize() {
                    return routeIds.size();
                }
            });
            // Check if all records are deleted successfully
            if (Arrays.stream(batchResult).allMatch(result -> result >= 0)) {
                logger.info("Successfully deleted {} route details for school code: {}", routeIds.size(), schoolCode);
                return "Route details deleted successfully";
            } else {
                throw new RuntimeException("Error occurred while deleting records!");
            }
        }catch (DataAccessException e){
            logger.error("Database error while deleting the route details: {} for school code: {}", routeIds, schoolCode, e);
            throw new RuntimeException("Bulk delete failed: " + e.getMessage(), e);
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }

    @Override
    public List<AddRouteDetails> getRouteDetailsBySearchText(String searchText, String schoolCode) throws Exception {
  String sql = "select ar.route_id, av.vehicle_number, ar.boarding_point, ar.destination, ar.max_fee,\n" +
          "ar.route_name, ar.route_code, ar.total_distance, ar.estimated_time,\n" +
          "ar.stop_name, ar.stop_fare from add_route as ar join add_vehicle as av ON ar.vehicle_id = av.vehicle_id WHERE CONCAT_WS(' ', ar.route_id, av.vehicle_number, ar.boarding_point, ar.destination, ar.max_fee, ar.stop_name, ar.stop_fare) ILIKE ? AND ar.end_date >= CURRENT_DATE";

        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddRouteDetails> addRouteDetails = null;
        try{
            addRouteDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<AddRouteDetails>() {
                @Override
                public AddRouteDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddRouteDetails ard = new AddRouteDetails();
                    ard.setRouteId(rs.getInt("route_id"));
                    ard.setVehicleNumber(rs.getString("vehicle_number"));
                    ard.setBoardingPoint(rs.getString("boarding_point"));
                    ard.setDestination(rs.getString("destination"));
                    ard.setMaxFee(rs.getBigDecimal("max_fee"));
//                    ====-------------------------------------------------------------added and maped by karan----------------------
                    ard.setRouteName(rs.getString("route_name"));
                    ard.setRouteCode(rs.getString("route_code"));
                    ard.setTotalDistance((Double) rs.getObject("total_distance"));
                    ard.setEstimatedTime((Integer) rs.getObject("estimated_time"));
//                    ================================-----------------------------------------------------------------------------only above fields
                    ard.setStopName(rs.getString("stop_name"));
                    ard.setStopFare(rs.getBigDecimal("stop_fare"));
                    return ard;
                }
            });
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch(Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addRouteDetails;
    }

    @Override
    public RouteGroupDto getGroupedRoutes(String hashValue, String schoolCode) {
        String sql = """
                SELECT ar.route_id, ar.vehicle_id, av.vehicle_number, ar.boarding_point, ar.destination, ar.max_fee, ar.stop_name, ar.stop_fare, ar.start_date, ar.end_date, ar.hash_value  ,ar.route_name, ar.route_code, ar.total_distance, ar.estimated_time
                FROM add_route ar
                JOIN add_vehicle av ON ar.vehicle_id = av.vehicle_id
                WHERE ar.hash_value = ?
                  AND ar.end_date >= CURRENT_DATE
                ORDER BY ar.route_id;
                """;
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);

        List<Map<String,Object>> rows = jdbcTemplate.queryForList(sql, hashValue);
        if(rows.isEmpty()) return null;

        RouteGroupDto dto = new RouteGroupDto();
        dto.setHashValue(hashValue);
        dto.setStops(new ArrayList<>());
        for(int i = 0; i< rows.size(); i++){
            Map<String, Object> r = rows.get(i);
            if(i == 0){
                dto.setVehicleId((Integer) r.get("vehicle_id"));
                dto.setVehicleNumber((String) r.get("vehicle_number"));
                dto.setBoardingPoint((String) r.get("boarding_point"));
                dto.setDestination((String) r.get("destination"));
                dto.setMaxFee((BigDecimal) r.get("max_fee"));
            }

            RouteStopDTO stop = new RouteStopDTO();
            stop.setRouteId((Integer) r.get("route_id"));
            stop.setStopName((String) r.get("stop_name"));
            stop.setStopFare((BigDecimal) r.get("stop_fare"));

            dto.getStops().add(stop);
        }
        return dto;
    }

    @Override
    public RouteGroupDto updateRouteByHash(RouteGroupDto dto, String schoolCode) throws Exception {
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            //1 Update parent route fields (all rows under same hash)
            String parentSql = "UPDATE add_route SET vehicle_id=?, boarding_point=?, destination=?, max_fee=?, route_name=?, route_code=?, total_distance=?, estimated_time=?  WHERE hash_value=?";
            jdbcTemplate.update(parentSql,
                    dto.getVehicleId(),
                    dto.getBoardingPoint(),
                    dto.getDestination(),
                    dto.getMaxFee(),
                    dto.getRouteName(),
                    dto.getRouteCode(),
                    dto.getTotalDistance(),
                    dto.getEstimatedTime(),
                    dto.getHashValue()
            );
            //2 Update each stop row
            String stopSql = "UPDATE add_route SET stop_name=?, stop_fare=? WHERE route_id=?";
            for(RouteStopDTO stop : dto.getStops()){
                jdbcTemplate.update(stopSql,
                        stop.getStopName(),
                        stop.getStopFare(),
                        stop.getRouteId()
                );
            }
            return dto;
        } catch (Exception e){
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
}
