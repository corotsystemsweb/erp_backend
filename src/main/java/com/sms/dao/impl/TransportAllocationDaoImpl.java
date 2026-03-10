package com.sms.dao.impl;

import com.sms.dao.TransportAllocationDao;
import com.sms.model.TransportAllocationDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
public class TransportAllocationDaoImpl implements TransportAllocationDao {
    private final JdbcTemplate jdbcTemplate;

    public TransportAllocationDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
     /*
    @method addTransportAllocation
    @param transportAllocationDetails
    @throws Exception
    @description add transport allocation in transport_allocation
    @developer Sukhendu Bhowmik
    */

    @Override
    public TransportAllocationDetails addTransportAllocation(TransportAllocationDetails transportAllocationDetails, String schoolCode) throws Exception {
        String sql = "insert into transport_allocation (school_id, driver_id, vehicle_id, route_id) values(?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, transportAllocationDetails.getSchoolId());
                ps.setInt(2, transportAllocationDetails.getDriverId());
                ps.setInt(3, transportAllocationDetails.getVehicleId());
                ps.setInt(4, transportAllocationDetails.getRouteId());
                return ps;

            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("ta_id")) {
                int generatedId = ((Number) keys.get("ta_id")).intValue();
                transportAllocationDetails.setTaId(generatedId);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return transportAllocationDetails;
    }

    @Override
    public TransportAllocationDetails getTransportAllocationById(int taId, String schoolCode) throws Exception {
        String sql = "select distinct ta.ta_id, ta.school_id, ta.driver_id, ta.vehicle_id, ta.route_id, concat(ad.first_name, ' ' ,ad.last_name) as driver_name, ad.contact_number, ad.license_number, av.vehicle_number, av.number_of_seat, av.vehicle_type, ar.boarding_point, ar.destination from transport_allocation ta join add_driver ad on ta.driver_id = ad.driver_id join add_vehicle av on ta.vehicle_id = av.vehicle_id join add_route ar on ta.route_id = ar.route_id where ta_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        TransportAllocationDetails transportAllocationDetails = null;
        try{
            transportAllocationDetails = jdbcTemplate.queryForObject(sql, new Object[]{taId}, new RowMapper<TransportAllocationDetails>() {
                @Override
                public TransportAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TransportAllocationDetails tad = new TransportAllocationDetails();
                    tad.setTaId(rs.getInt("ta_id"));
                    tad.setSchoolId(rs.getInt("school_id"));
                    tad.setDriverId(rs.getInt("driver_id"));
                    tad.setVehicleId(rs.getInt("vehicle_id"));
                    tad.setRouteId(rs.getInt("route_id"));
                    tad.setDriverName(rs.getString("driver_name"));
                    tad.setDriverContactNumber(CipherUtils.decrypt(rs.getString("contact_number")));
                    tad.setLicenseNumber(CipherUtils.decrypt(rs.getString("license_number")));
                    tad.setVehicleNumber(rs.getString("vehicle_number"));
                    tad.setNoOfSeats(rs.getInt("number_of_seat"));
                    tad.setVehicleType(rs.getString("vehicle_type"));
                    tad.setBoardingPoint(rs.getString("boarding_point"));
                    tad.setDestination(rs.getString("destination"));
                    return tad;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return transportAllocationDetails;
    }

    @Override
    public List<TransportAllocationDetails> getAllTransportAllocation(String schoolCode) throws Exception {
        String sql = "select distinct ta.ta_id, ta.school_id, ta.driver_id, ta.vehicle_id, ta.route_id, concat(ad.first_name, ' ' ,ad.last_name) as driver_name, ad.contact_number, ad.license_number, av.vehicle_number, av.number_of_seat, av.vehicle_type, ar.boarding_point, ar.destination from transport_allocation ta join add_driver ad on ta.driver_id = ad.driver_id join add_vehicle av on ta.vehicle_id = av.vehicle_id join add_route ar on ta.route_id = ar.route_id order by ta.ta_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<TransportAllocationDetails> transportAllocationDetails = null;
        try{
            transportAllocationDetails = jdbcTemplate.query(sql, new RowMapper<TransportAllocationDetails>() {
                @Override
                public TransportAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TransportAllocationDetails tad = new TransportAllocationDetails();
                    tad.setTaId(rs.getInt("ta_id"));
                    tad.setSchoolId(rs.getInt("school_id"));
                    tad.setDriverId(rs.getInt("driver_id"));
                    tad.setVehicleId(rs.getInt("vehicle_id"));
                    tad.setRouteId(rs.getInt("route_id"));
                    tad.setDriverName(rs.getString("driver_name"));
                    tad.setDriverContactNumber(CipherUtils.decrypt(rs.getString("contact_number")));
                    tad.setLicenseNumber(CipherUtils.decrypt(rs.getString("license_number")));
                    tad.setVehicleNumber(rs.getString("vehicle_number"));
                    tad.setNoOfSeats(rs.getInt("number_of_seat"));
                    tad.setVehicleType(rs.getString("vehicle_type"));
                    tad.setBoardingPoint(rs.getString("boarding_point"));
                    tad.setDestination(rs.getString("destination"));
                    return tad;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return transportAllocationDetails;
    }

   @Override
   public TransportAllocationDetails updateTransportAllocation(TransportAllocationDetails transportAllocationDetails, int taId, String schoolCode) throws Exception {
       String sql = "update transport_allocation set driver_id = ?, vehicle_id = ?, route_id = ? where ta_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql,
                   transportAllocationDetails.getDriverId(),
                   transportAllocationDetails.getVehicleId(),
                   transportAllocationDetails.getRouteId(),
                   taId);
           if(rowAffected > 0){
               return transportAllocationDetails;
           }
           return null;
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }

   }

   @Override
   public boolean deleteTransportAllocation(int taId, String schoolCode) throws Exception {
       String sql = "delete from transport_allocation where ta_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql, new Object[]{taId});
           if(rowAffected > 0 ){
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
    public List<TransportAllocationDetails> getTransportAllocationDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "select av.vehicle_number, ad.first_name, ad.last_name, ad.contact_number, ar.boarding_point, ar.destination from transport_allocation ta join add_driver ad on ta.driver_id = ad.driver_id join add_vehicle av on ta.vehicle_id = av.vehicle_id join add_route ar on ta.route_id = ar.route_id ";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<TransportAllocationDetails> transportAllocationDetails = null;
        try{
            transportAllocationDetails = jdbcTemplate.query(sql, new RowMapper<TransportAllocationDetails>() {
                @Override
                public TransportAllocationDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    TransportAllocationDetails tad = new TransportAllocationDetails();
                    //tad.setTaId(rs.getInt("ta_id"));
                    tad.setVehicleNumber(rs.getString("vehicle_number"));
                    tad.setDriverFirstName(rs.getString("first_name"));
                    tad.setDriverLastName(rs.getString("last_name"));
                    tad.setDriverContactNumber(CipherUtils.decrypt(rs.getString("contact_number")));
                    tad.setBoardingPoint(rs.getString("boarding_point"));
                    tad.setDestination(rs.getString("destination"));

                    // Create a combined string of all fields
                    String combinedData = tad.getVehicleNumber() + " " + tad.getDriverFirstName() + " " + tad.getDriverLastName() + " " + tad.getDriverContactNumber() + " " + tad.getBoardingPoint() + " " + tad.getDestination();

                    // Convert to lowercase for case-insensitive matching
                    combinedData = combinedData.toLowerCase();

                    // Check if the combined string contains the search text
                    if(combinedData.contains(searchText.toLowerCase())){
                        return  tad;
                    }else {
                        return null;
                    }
                }
            }).stream().filter(Objects::nonNull).collect(Collectors.toList());
        }catch(EmptyResultDataAccessException e){
            return null;
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return transportAllocationDetails;
    }
}
