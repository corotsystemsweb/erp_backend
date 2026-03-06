package com.sms.service;


import com.sms.model.StudentExcelDetails;
import org.springframework.core.io.InputStreamResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.springframework.core.io.Resource;
public interface StudentExcelService {
    public List<StudentExcelDetails> processExcelFileForStudentExcelDetails(MultipartFile file, String schoolCode) throws Exception;
    public InputStreamResource validateAndFetchFile(String fileName) throws IOException;
}
