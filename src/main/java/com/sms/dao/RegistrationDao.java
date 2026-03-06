package com.sms.dao;

import com.sms.model.RegistrationDetails;

public interface RegistrationDao {
    public int checkEmailExists(String email);
    public RegistrationDetails registration(RegistrationDetails registrationDetails) throws Exception;
    public RegistrationDetails login(String email, String password);
    public RegistrationDetails getUserByEmail(String email);
}
