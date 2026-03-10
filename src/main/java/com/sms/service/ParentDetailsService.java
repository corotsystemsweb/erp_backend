package com.sms.service;

import com.sms.model.ParentDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ParentDetailsService {
    Map<String, String> loadImage(String schoolCode, int parentId, String parentType);
    public List<ParentDetails> addBulkParentDetails(List<ParentDetails> parentList, Map<String, byte[]> imageMap, String schoolCode) throws Exception;
    public List<ParentDetails> getAllParentDetails(String schoolCode) throws Exception;
    public ParentDetails getParentDetailsById(int parentId, String schoolCode) throws Exception;
    public List<ParentDetails> updateBulkParentDetailsById(List<ParentDetails> parentList, Map<String, byte[]> imageMap, String schoolCode) throws Exception;
    public boolean softDeleteBulkParentDetails(List<Integer> parentIds, String schoolCode) throws Exception;
    public List<ParentDetails> getParentsByUuid(String uuid, String schoolCode) throws Exception;
}
