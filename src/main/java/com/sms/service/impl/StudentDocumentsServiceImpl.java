package com.sms.service.impl;

import com.sms.service.StudentDocumentsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class StudentDocumentsServiceImpl implements StudentDocumentsService {
    @Value("${student.documents.local.path}")
    private String FOLDER_PATH;

    private static final long MAX_SIZE = 100 * 1024; //100 KB
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png");

    @Override
    public boolean uploadDocuments(Map<String, MultipartFile> documents, int studentId, String schoolCode) throws Exception {
        if(documents == null || documents.isEmpty()){
            return true;
        }
        String baseDirPath = FOLDER_PATH + File.separator + schoolCode;

        File baseDir = new File(baseDirPath);
        if(!baseDir.exists()){
            baseDir.mkdirs();
        }

        for(Map.Entry<String, MultipartFile> entry : documents.entrySet()){
            MultipartFile file = entry.getValue();
            if(file == null || file.isEmpty()){
                continue; // optional document
            }

            // size validation
            if(file.getSize() > MAX_SIZE){
                throw new IllegalArgumentException(entry.getKey() + " exceeds maximum size of 100KB");
            }

            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                throw new IllegalArgumentException("Invalid file name for " + entry.getKey());
            }

            String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();

            // Extension validation
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new IllegalArgumentException("Only JPG, JPEG, PNG allowed for " + entry.getKey());
            }

            // Convert key to snake_case
            String documentName = convertToSnakeCase(entry.getKey());

            String filePath = baseDirPath + File.separator + documentName + "_" + studentId + "." + extension;

            file.transferTo(new File(filePath));
        }
        return true;
    }

    // Helper method
    private String convertToSnakeCase(String input) {
        return input
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    @Override
    public Map<String, String> downloadDocuments(int studentId, String schoolCode) throws Exception {
        Map<String, String> documentMap = new HashMap<>();
        String baseDirPath = FOLDER_PATH + File.separator + schoolCode;

        File baseDir = new File(baseDirPath);
        if (!baseDir.exists()) {
            return documentMap;
        }
        File[] files = baseDir.listFiles((dir, name) -> name.endsWith("_" + studentId + ".jpg") || name.endsWith("_" + studentId + ".jpeg") || name.endsWith("_" + studentId + ".png"));

        if (files == null) {
            return documentMap;
        }
        for (File file : files) {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            documentMap.put(file.getName(), base64);
        }

        return documentMap;
    }
}
