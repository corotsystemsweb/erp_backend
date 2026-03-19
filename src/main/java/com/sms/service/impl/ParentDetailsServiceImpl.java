package com.sms.service.impl;

import com.sms.dao.ParentDetailsDao;
import com.sms.model.ParentDetails;
import com.sms.service.ParentDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ParentDetailsServiceImpl implements ParentDetailsService {
    @Autowired
    private ParentDetailsDao parentDetailsDao;

    @Value("${parent.img.local.path}")
    private String folderPath;

    private void saveImage(String parentType, byte[] fileBytes, String schoolCode, int parentId) throws Exception {

        if (fileBytes == null || fileBytes.length == 0) return;

        //String cleanType = parentType.toLowerCase().replaceAll("\\s+", "_");
        String cleanType = parentType.toLowerCase().trim().replaceAll("\\s+", "_").replaceAll("_+", "_");

        File directory = new File(folderPath + schoolCode);
        if (!directory.exists()) directory.mkdirs();

        String filePath = folderPath + schoolCode + File.separator + cleanType + "_" + parentId + ".png";

        Files.write(Paths.get(filePath), fileBytes);
    }

    @Override
    public Map<String, String> loadImage(String schoolCode, int parentId, String parentType) {

        Map<String, String> result = new HashMap<>();

        String cleanType = parentType.toLowerCase().trim().replaceAll("\\s+", "_").replaceAll("_+", "_");

        File file = new File(folderPath + schoolCode + File.separator + cleanType + "_" + parentId + ".png");

        if (!file.exists()) return result;

        try {
            String base64 = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            result.put(cleanType, base64);
        } catch (Exception ignored) {}

        return result;
    }

    @Override
    public List<ParentDetails> addBulkParentDetails(List<ParentDetails> parentList, Map<String, byte[]> imageMap, String schoolCode) throws Exception {

        List<ParentDetails> savedParents = parentDetailsDao.addBulkParentDetails(parentList, schoolCode);

        for (ParentDetails parent : savedParents) {
            String cleanType = parent.getParentType().toLowerCase().replaceAll("\\s+", "_");

            // Find matching key in image map
            byte[] fileBytes = imageMap.get(cleanType);

            if (fileBytes != null) {
                saveImage(cleanType, fileBytes, schoolCode, parent.getParentId());
            }
            if (fileBytes != null && fileBytes.length > 0) {
                saveImage(cleanType, fileBytes, schoolCode, parent.getParentId());
            }

            // Load image back as base64
            Map<String, String> img = loadImage(schoolCode, parent.getParentId(), parent.getParentType());

            if (!img.isEmpty()) {
                parent.getParentImages().putAll(img);
            }
        }

        return savedParents;
    }

    @Override
    public List<ParentDetails> getAllParentDetails(String schoolCode) throws Exception {
        return  parentDetailsDao.getAllParentDetails(schoolCode);
    }

    @Override
    public ParentDetails getParentDetailsById(int parentId, String schoolCode) throws Exception {
       return parentDetailsDao.getParentDetailsById(parentId, schoolCode);
    }


    @Override
    public List<ParentDetails> updateBulkParentDetailsById(List<ParentDetails> parentList, Map<String, byte[]> imageMap, String schoolCode) throws Exception {
        List<ParentDetails> updatedParents = parentDetailsDao.updateBulkParentDetailsById(parentList, schoolCode);
        for (ParentDetails parent : updatedParents) {

            // SAME EXACT NORMALIZATION AS ADD
            String cleanType = parent.getParentType()
                    .toLowerCase()
                    .trim()
                    .replaceAll("\\s+", "_")
                    .replaceAll("_+", "_");

            // SAME KEY LOOKUP AS ADD
            byte[] bytes = imageMap.get(cleanType);

            // SAME SAVE LOGIC AS ADD
            if (bytes != null && bytes.length > 0) {
                saveImage(cleanType, bytes, schoolCode, parent.getParentId());
            }

            // SAME LOAD LOGIC AS ADD
            Map<String, String> img = loadImage(schoolCode, parent.getParentId(), parent.getParentType());

            if (!img.isEmpty()) {
                if (parent.getParentImages() == null)
                    parent.setParentImages(new HashMap<>());
                parent.getParentImages().putAll(img);
            }
        }

        return updatedParents;
    }

    @Override
    public boolean softDeleteBulkParentDetails(List<Integer> parentIds, String schoolCode) throws Exception {
        return parentDetailsDao.softDeleteBulkParentDetails(parentIds, schoolCode);
    }

    @Override
    public List<ParentDetails> getParentsByUuid(String uuid, String schoolCode) throws Exception {
        return parentDetailsDao.getParentsByUuid(uuid, schoolCode);
    }
}
