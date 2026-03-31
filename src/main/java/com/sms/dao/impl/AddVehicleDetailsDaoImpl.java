package com.sms.dao.impl;

import com.sms.dao.AddVehicleDetailsDao;
import com.sms.model.AddVehicleDetails;
import com.sms.model.StaffDetails;
import com.sms.util.CipherUtils;
import com.sms.util.DatabaseUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@Repository
public class AddVehicleDetailsDaoImpl implements AddVehicleDetailsDao {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AddVehicleDetailsDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public AddVehicleDetails addVehicle(AddVehicleDetails addVehicleDetails, String schoolCode) throws Exception {
        String sql = "insert into add_vehicle (school_id, vehicle_number, vehicle_type, number_of_seat, refuel_amount, last_insurance_date, renewal_insurance_date, last_service_date, model, manufacturer, year_of_manufacture, registration_date, fitness_expiry, status) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, addVehicleDetails.getSchoolId());
                ps.setString(2, addVehicleDetails.getVehicleNumber());
                ps.setString(3, addVehicleDetails.getVehicleType());
                ps.setInt(4, addVehicleDetails.getNumberOfSeat());
                ps.setDouble(5, addVehicleDetails.getRefuelAmount());
                ps.setDate(6, new java.sql.Date(addVehicleDetails.getLastInsuranceDate().getTime()));
                ps.setDate(7, new java.sql.Date(addVehicleDetails.getRenewalInsuranceDate().getTime()));
                ps.setDate(8, new java.sql.Date(addVehicleDetails.getLastServiceDate().getTime()));

//                ------------------------------------------------------added by karan-----------------------------------------------------------
                ps.setString(9, addVehicleDetails.getModel());
                ps.setString(10, addVehicleDetails.getManufacturer());
                ps.setInt(11, addVehicleDetails.getYearOfManufacture());
                ps.setDate(12, addVehicleDetails.getRegistrationDate() != null ? new java.sql.Date(addVehicleDetails.getRegistrationDate().getTime()) : null);
                ps.setDate(13, addVehicleDetails.getFitnessExpiry() != null ? new java.sql.Date(addVehicleDetails.getFitnessExpiry().getTime()) : null);
                ps.setString(14, addVehicleDetails.getStatus());
//                ---------------------------------------------------------------------------------------------------ende here --------------------------------------------------------------
                return ps;
            },keyHolder);
            Map<String, Object> keys = keyHolder.getKeys();
            if(keys != null && keys.containsKey("vehicle_id")){
                int generatedId = ((Number)keys.get("vehicle_id")).intValue();
                addVehicleDetails.setVehicleId(generatedId);
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addVehicleDetails;
    }

    @Override
    public AddVehicleDetails getVehicleById(int vehicleId, String schoolCode) throws Exception {
        String sql = "select vehicle_id, school_id, vehicle_number, vehicle_type, number_of_seat, refuel_amount, last_insurance_date, renewal_insurance_date, last_service_date , model, manufacturer, year_of_manufacture, registration_date, fitness_expiry, status from add_vehicle where vehicle_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{vehicleId}, new RowMapper<AddVehicleDetails>() {
                @Override
                public AddVehicleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddVehicleDetails avd = new AddVehicleDetails();
                    avd.setVehicleId(rs.getInt("vehicle_id"));
                    avd.setSchoolId(rs.getInt("school_id"));
                    avd.setVehicleNumber(rs.getString("vehicle_number"));
                    avd.setVehicleType(rs.getString("vehicle_type"));
                    avd.setNumberOfSeat(rs.getInt("number_of_seat"));
                    avd.setRefuelAmount(rs.getDouble("refuel_amount"));
                    avd.setLastInsuranceDate(rs.getDate("last_insurance_date"));
                    avd.setRenewalInsuranceDate(rs.getDate("renewal_insurance_date"));
                    avd.setLastServiceDate(rs.getDate("last_service_date"));
//                    ------------------------------here added by karan------------------------------------------------------------------
                    avd.setModel(rs.getString("model"));
                    avd.setManufacturer(rs.getString("manufacturer"));
                    avd.setYearOfManufacture(rs.getInt("year_of_manufacture"));
                    avd.setRegistrationDate(rs.getDate("registration_date"));
                    avd.setFitnessExpiry(rs.getDate("fitness_expiry"));
                    avd.setStatus(rs.getString("status"));
//                    -----------------------------------------------------ends here-----------------------------------------------------------
                    return avd;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
    }
   @Override
   public List<AddVehicleDetails> getAllVehicle(String schoolCode) throws Exception {
       String sql = "select vehicle_id, school_id, vehicle_number, vehicle_type, number_of_seat, refuel_amount, last_insurance_date, renewal_insurance_date, last_service_date , model, manufacturer, year_of_manufacture, registration_date, fitness_expiry, status from add_vehicle order by vehicle_id asc";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       List<AddVehicleDetails> addVehicleDetails = null;
       try{
           addVehicleDetails = jdbcTemplate.query(sql, new RowMapper<AddVehicleDetails>() {
               @Override
               public AddVehicleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                   AddVehicleDetails avd = new AddVehicleDetails();
                   avd.setVehicleId(rs.getInt("vehicle_id"));
                   avd.setSchoolId(rs.getInt("school_id"));
                   avd.setVehicleNumber(rs.getString("vehicle_number"));
                   avd.setVehicleType(rs.getString("vehicle_type"));
                   avd.setNumberOfSeat(rs.getInt("number_of_seat"));
                   avd.setRefuelAmount(rs.getDouble("refuel_amount"));
                   avd.setLastInsuranceDate(rs.getDate("last_insurance_date"));
                   avd.setRenewalInsuranceDate(rs.getDate("renewal_insurance_date"));
                   avd.setLastServiceDate(rs.getDate("last_service_date"));

//                   ---------------------------------------------------------------------------------------------added by karan---------------------------------------------
                   avd.setModel(rs.getString("model"));
                   avd.setManufacturer(rs.getString("manufacturer"));
                   avd.setYearOfManufacture(rs.getInt("year_of_manufacture"));
                   avd.setRegistrationDate(rs.getDate("registration_date"));
                   avd.setFitnessExpiry(rs.getDate("fitness_expiry"));
                   avd.setStatus(rs.getString("status"));
                   return avd;

               }
           });
       }catch (Exception e){
           e.printStackTrace();
           return null;
       }finally {
           DatabaseUtil.closeDataSource(jdbcTemplate);
       }
       return addVehicleDetails;
   }
   @Override
   public AddVehicleDetails updateVehicle(AddVehicleDetails addVehicleDetails, int vehicleId, String schoolCode) throws Exception {
       String sql = "update add_vehicle set vehicle_number = ?, vehicle_type = ?, number_of_seat = ?, refuel_amount = ?, last_insurance_date = ?, renewal_insurance_date = ?, last_service_date = ?, model=?, manufacturer=?, year_of_manufacture=?, registration_date=?, fitness_expiry=?, status=? where vehicle_id = ?";
       JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
       try{
           int rowAffected = jdbcTemplate.update(sql,
                   addVehicleDetails.getVehicleNumber(),
                   addVehicleDetails.getVehicleType(),
                   addVehicleDetails.getNumberOfSeat(),
                   addVehicleDetails.getRefuelAmount(),
                   addVehicleDetails.getLastInsuranceDate(),
                   addVehicleDetails.getRenewalInsuranceDate(),
                   addVehicleDetails.getLastServiceDate(),

//                   ---------------------------------------------------------------------------------------------added by karan---------------------------------------
                   addVehicleDetails.getModel(),
                   addVehicleDetails.getManufacturer(),
                   addVehicleDetails.getYearOfManufacture(),
                   addVehicleDetails.getRegistrationDate(),
                   addVehicleDetails.getFitnessExpiry(),
                   addVehicleDetails.getStatus(),

//                   ------------------------------------------------------------------------------------------------ends here----------------------------
                   vehicleId
           );
           if(rowAffected > 0){
               return addVehicleDetails;
           }else{
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
    public boolean deleteVehicle(int vehicleId, String schoolCode) throws Exception {
        String sql = "delete from add_vehicle where vehicle_id = ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        try{
            int rowAffected = jdbcTemplate.update(sql, new Object[]{vehicleId});
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
    public List<AddVehicleDetails> getAllVehicleNumber(String schoolCode) throws Exception {
        String sql = "select vehicle_id, vehicle_number, vehicle_type, number_of_seat from add_vehicle order by vehicle_id asc";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddVehicleDetails> addVehicleDetails = null;
        try{
            addVehicleDetails = jdbcTemplate.query(sql, new RowMapper<AddVehicleDetails>() {
                @Override
                public AddVehicleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddVehicleDetails avd = new AddVehicleDetails();
                    avd.setVehicleId(rs.getInt("vehicle_id"));
                    avd.setVehicleNumber(rs.getString("vehicle_number"));
                    avd.setVehicleType(rs.getString("vehicle_type"));
                    avd.setNumberOfSeat(rs.getInt("number_of_seat"));
                    return avd;
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }finally {
            DatabaseUtil.closeDataSource(jdbcTemplate);
        }
        return addVehicleDetails;
    }

    @Override
    public List<AddVehicleDetails> getVehicleDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        String sql = "select vehicle_id, vehicle_type, vehicle_number, refuel_amount, last_service_date, last_insurance_date, renewal_insurance_date , model, manufacturer, year_of_manufacture, registration_date, fitness_expiry, status from add_vehicle where CONCAT_WS(' ', vehicle_id, vehicle_type, vehicle_number, refuel_amount, last_service_date, last_insurance_date, renewal_insurance_date, model, manufacturer, year_of_manufacture, registration_date, fitness_expiry, status) ILIKE ?";
        JdbcTemplate jdbcTemplate = DatabaseUtil.getJdbctemplateForSchool(schoolCode);
        List<AddVehicleDetails> addVehicleDetails = null;
        try{
            addVehicleDetails = jdbcTemplate.query(sql, new Object[]{"%" + searchText + "%"}, new RowMapper<AddVehicleDetails>() {
                @Override
                public AddVehicleDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AddVehicleDetails avd = new AddVehicleDetails();
                    avd.setVehicleId(rs.getInt("vehicle_id"));
                    avd.setVehicleType(rs.getString("vehicle_type"));
                    avd.setVehicleNumber(rs.getString("vehicle_number"));
                    avd.setRefuelAmount(rs.getDouble("refuel_amount"));
                    avd.setLastServiceDate(rs.getDate("last_service_date"));
                    avd.setLastInsuranceDate(rs.getDate("last_insurance_date"));
                    avd.setRenewalInsuranceDate(rs.getDate("renewal_insurance_date"));

//                    --------------------------------------------------------------------added by karan--------------------------------------------
                    // ADD THESE LINES:
                    avd.setModel(rs.getString("model"));
                    avd.setManufacturer(rs.getString("manufacturer"));
                    avd.setYearOfManufacture(rs.getInt("year_of_manufacture"));
                    avd.setRegistrationDate(rs.getDate("registration_date"));
                    avd.setFitnessExpiry(rs.getDate("fitness_expiry"));
                    avd.setStatus(rs.getString("status"));
                    return avd;
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
        return addVehicleDetails;
    }
}
