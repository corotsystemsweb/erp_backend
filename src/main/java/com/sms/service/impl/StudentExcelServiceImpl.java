package com.sms.service.impl;

import com.sms.dao.StudentExcelDao;
import com.sms.model.StudentExcelDetails;
import com.sms.service.StudentExcelService;
import com.sms.util.StudentExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class StudentExcelServiceImpl implements StudentExcelService {
    @Autowired
    private StudentExcelDao studentExcelDao;
    @Value("${student.excel.path}")
    private String FILE_DIRECTORY;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public List<StudentExcelDetails> processExcelFileForStudentExcelDetails(MultipartFile file, String schoolCode) throws Exception {
        studentExcelDao.truncateStudentExcelTable(schoolCode);
        List<StudentExcelDetails> students = StudentExcelHelper.convertExcelToListOfStudentExcelDetails(file.getInputStream());
        List<StudentExcelDetails> updatedStudents = new ArrayList<>();
        for (StudentExcelDetails student : students) {
            // Generate a UUID
            String uuid = UUID.randomUUID().toString();
            // Assign UUID to the student and save to student_excel
            student.setUuid(uuid);
            studentExcelDao.addData(student, schoolCode);
            updatedStudents.add(student);
        }
        return updatedStudents;
    }

    public InputStreamResource validateAndFetchFile(String fileName) throws IOException {
            // Construct file path
            String filePath = FILE_DIRECTORY + fileName;

            // Check if file exists
            Path file = Paths.get(filePath);
            if (!Files.exists(file)) {
                throw new IOException("File not found");
            }

            // Check file size
            long fileSize = Files.size(file);
            if (fileSize > 10 * 1024 * 1024) { // 10 MB
                throw new IOException("File size exceeds the limit");
            }

            // Read file into InputStreamResource
            return new InputStreamResource(Files.newInputStream(file));
        }
}
