package com.sms.dao;

import com.sms.model.AddSupplierDetails;

import java.util.List;

public interface AddSupplierDao {
    public AddSupplierDetails addSupplierDetails(AddSupplierDetails addSupplierDetails, String schoolCode) throws  Exception;

    public AddSupplierDetails getSupplierDetailsById(int supplierId, String schoolCode) throws Exception;
    public List<AddSupplierDetails> getSupplierDetails(String schoolCode) throws Exception;
    public AddSupplierDetails updateSupplierDetails(AddSupplierDetails addSupplierDetails, int supplierId, String schoolCode) throws Exception;
    public boolean softDeleteSupplierDetails(int supplierId,String schoolCode) throws Exception;
}
