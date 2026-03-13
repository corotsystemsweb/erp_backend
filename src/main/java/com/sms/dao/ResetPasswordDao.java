package com.sms.dao;

import com.sms.model.ResetPasswordDetails;

public interface ResetPasswordDao {
    public int checkEmailExist(String email) throws Exception;
    public void updatePassword(String email, String encodedPassword) throws Exception;
}
