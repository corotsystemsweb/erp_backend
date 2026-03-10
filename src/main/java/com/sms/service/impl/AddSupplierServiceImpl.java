package com.sms.service.impl;

import com.sms.dao.AddSupplierDao;
import com.sms.model.AddSupplierDetails;
import com.sms.service.AddSupplierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddSupplierServiceImpl implements AddSupplierService {
    @Autowired
    private AddSupplierDao addSupplierDao;
    @Override
    public AddSupplierDetails addSupplierDetails(AddSupplierDetails addSupplierDetails, String schoolCode) throws Exception {
        return addSupplierDao.addSupplierDetails(addSupplierDetails,schoolCode);
    }

    @Override
    public AddSupplierDetails getSupplierDetailsById(int supplierId, String schoolCode) throws Exception {
        return addSupplierDao.getSupplierDetailsById(supplierId,schoolCode);
    }

    @Override
    public List<AddSupplierDetails> getSupplierDetails(String schoolCode) throws Exception {
        return addSupplierDao.getSupplierDetails(schoolCode);
    }

    @Override
    public AddSupplierDetails updateSupplierDetails(AddSupplierDetails addSupplierDetails, int supplierId, String schoolCode) throws Exception {
        return addSupplierDao.updateSupplierDetails(addSupplierDetails,supplierId,schoolCode);
    }

    @Override
    public boolean softDeleteSupplierDetails(int supplierId, String schoolCode) throws Exception {
        return addSupplierDao.softDeleteSupplierDetails(supplierId,schoolCode);
    }
}
