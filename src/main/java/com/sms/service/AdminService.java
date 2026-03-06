package com.sms.service;

import com.sms.model.AdminDetails;

import java.util.List;

public interface AdminService {
    public boolean addAdmin(AdminDetails adminDetails) throws Exception;
    public AdminDetails getAdminDetailsById(int id) throws Exception;
    public List<AdminDetails> getAllAdminDetails() throws Exception;
}
