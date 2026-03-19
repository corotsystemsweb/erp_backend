package com.sms.dao;

import com.sms.model.ParentDetails;

import java.util.List;

public interface ParentDetailsDao {
    public List<ParentDetails> addBulkParentDetails(List<ParentDetails> parentDetailsList, String schoolCode) throws Exception;

    public List<ParentDetails> getAllParentDetails(String schoolCode) throws Exception;

    public ParentDetails getParentDetailsById(int parentId, String schoolCode);

    public List<ParentDetails> updateBulkParentDetailsById(List<ParentDetails> parentDetailsList, String schoolCode) throws Exception;
    public boolean softDeleteBulkParentDetails(List<Integer> parentIds, String schoolCode) throws Exception;
    public List<ParentDetails> getParentsByUuid(String uuid, String schoolCode) throws Exception;
}
