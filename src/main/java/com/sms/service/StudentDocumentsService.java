package com.sms.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface StudentDocumentsService {
    boolean uploadDocuments(Map<String, MultipartFile> documents, int studentId, String schoolCode) throws Exception;
    Map<String, String> downloadDocuments(int studentId, String schoolCode) throws Exception;
}
