package com.sms.service.impl;

import com.sms.dao.UserRegistrationDao;
import com.sms.model.UserRegistrationDetails;
import com.sms.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
    @Autowired
    private UserRegistrationDao userRegistrationDao;

    @Override
    public UserRegistrationDetails adduser(UserRegistrationDetails userRegistrationDetails) throws Exception {
        //check email is exists or not
        int count = userRegistrationDao.checkEmailExists(userRegistrationDetails.getEmail());
        if(count > 0){
            throw new IllegalArgumentException("Email is already exist ");
        }
        if(!userRegistrationDetails.getPassword().equals(userRegistrationDetails.getConfirmPassword())){
            throw new IllegalArgumentException("Password and Confirm Password do not match");
        }
        return userRegistrationDao.adduser(userRegistrationDetails);
    }

    @Override
    public List<UserRegistrationDetails> getAllUserDetails(String schoolCode) throws Exception {
        return userRegistrationDao.getAllUserDetails(schoolCode);
    }

    @Override
    public UserRegistrationDetails getUserDetailsById(int userId, String schoolCode) throws Exception {
        return userRegistrationDao.getUserDetailsById(userId, schoolCode);
    }

    @Override
    public String updateUserDetailsById(UserRegistrationDetails userRegistrationDetails, int userId, String schoolCode) throws Exception {
        return userRegistrationDao.updateUserDetailsById(userRegistrationDetails, userId, schoolCode);
    }

    @Override
    public boolean deleteUser(int userId, String schoolCode) throws Exception {
        return userRegistrationDao.deleteUser(userId, schoolCode);
    }

    @Override
    public List<UserRegistrationDetails> getAllRole() throws Exception {
        return userRegistrationDao.getAllRole();
    }
}
