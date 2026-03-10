package com.sms.dao;

import com.sms.model.UserRegistrationDetails;

import java.util.List;

public interface UserRegistrationDao {
    public int checkEmailExists(String email);
    public UserRegistrationDetails adduser(UserRegistrationDetails registrationDetails) throws Exception;
    public List<UserRegistrationDetails> getAllUserDetails(String schoolCode) throws Exception;
    public UserRegistrationDetails getUserDetailsById(int userId, String schoolCode) throws Exception;
    public String updateUserDetailsById(UserRegistrationDetails userRegistrationDetails, int userId, String schoolCode) throws Exception;
    public boolean deleteUser(int userId, String schoolCode) throws Exception;
    public List<UserRegistrationDetails> getAllRole() throws Exception;
}
