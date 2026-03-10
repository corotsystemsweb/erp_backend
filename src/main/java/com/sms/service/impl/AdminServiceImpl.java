package com.sms.service.impl;

import com.sms.dao.AdminDao;
import com.sms.model.AdminDetails;
import com.sms.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private  AdminDao adminDao;
    @Override
    public boolean addAdmin(AdminDetails adminDetails) throws Exception {
        return adminDao.addAdmin(adminDetails);
    }

    @Override
    public AdminDetails getAdminDetailsById(int id) throws Exception {
        return adminDao.getAdminDetailsById(id);
    }

    @Override
    public List<AdminDetails> getAllAdminDetails() throws Exception {
        return adminDao.getAllAdminDetails();
    }
}
