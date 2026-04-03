package com.sms.service.impl;

import com.sms.dao.StudentEnquiryFormDao;
import com.sms.model.StudentEnquiryFormDetails;
import com.sms.service.StudentEnquiryFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentEnquiryFormServiceImpl implements StudentEnquiryFormService {

    @Autowired
    private StudentEnquiryFormDao studentEnquiryFormDao;


    @Override
    public StudentEnquiryFormDetails saveStudentEnquiry(StudentEnquiryFormDetails studentEnquiryFormDetails, MultipartFile file, String schoolCode) throws Exception {
        //Save student enquiry data
        StudentEnquiryFormDetails savedData = studentEnquiryFormDao.saveStudentEnquiry(studentEnquiryFormDetails, schoolCode);

        int studentId = savedData.getStudentEnquiryId();

        // Save image (OPTIONAL)
        if (file != null && !file.isEmpty()) {

            // Save image
            studentEnquiryFormDao.addImage(file, schoolCode, studentId);

            // Fetch image as Base64
            StudentEnquiryFormDetails imgData = studentEnquiryFormDao.getImage(schoolCode, studentId);

            // Set image in response
            if (imgData != null && imgData.getStudentImage() != null) {
                savedData.setStudentImage(imgData.getStudentImage());
            }
        }

        return savedData;

    }

    @Override
    public List<StudentEnquiryFormDetails> getAllStudentEnquiry(String status, String schoolCode) throws Exception {
        List<StudentEnquiryFormDetails> list = studentEnquiryFormDao.getAllStudentEnquiry(status, schoolCode);

        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        List<StudentEnquiryFormDetails> finalList = new ArrayList<>();

        for(StudentEnquiryFormDetails combined : list){
            int studentId = combined.getStudentEnquiryId();

            //Load student photo
            try{
                StudentEnquiryFormDetails img = studentEnquiryFormDao.getImage(schoolCode, studentId);
                if (img != null && img.getStudentImage() != null) {
                    combined.setStudentImage(img.getStudentImage());
                }
            } catch (Exception ignored) {}

            finalList.add(combined);
        }

        return finalList;
    }

    @Override
    public StudentEnquiryFormDetails getStudentEnquiryById(int studentEnquiryId, String schoolCode) throws Exception {
        StudentEnquiryFormDetails student =  studentEnquiryFormDao.getStudentEnquiryById(studentEnquiryId, schoolCode);

        if(student == null){
            return null;
        }

        //Load student image
        try{
            StudentEnquiryFormDetails img = studentEnquiryFormDao.getImage(schoolCode, studentEnquiryId);
            if(img != null && img.getStudentImage() != null){
                student.setStudentImage(img.getStudentImage());
            }
        } catch (Exception Ignored){
            // Optional: log this instead of ignoring
        }
        return student;
    }

    @Override
    public StudentEnquiryFormDetails updateStudentEnquiryById(StudentEnquiryFormDetails studentEnquiryFormDetails, MultipartFile file, String schoolCode) throws Exception {

        // Update student data
        StudentEnquiryFormDetails student = studentEnquiryFormDao.updateStudentEnquiryById(studentEnquiryFormDetails, schoolCode);

        // If not found
        if (student == null) {
            return null;
        }

        int studentId = student.getStudentEnquiryId();

        //Update image if file provided
        if (file != null && !file.isEmpty()) {
            try {
                // Save/replace image
                studentEnquiryFormDao.addImage(file, schoolCode, studentId);
            } catch (Exception ignored) {
                // no logging (as per your requirement)
            }
        }

        //Fetch image (always, whether updated or existing)
        try {
            StudentEnquiryFormDetails img = studentEnquiryFormDao.getImage(schoolCode, studentId);

            if (img != null && img.getStudentImage() != null) {
                student.setStudentImage(img.getStudentImage());
            }
        } catch (Exception ignored) {
            // image optional
        }

        return student;
    }

    @Override
    public boolean deleteStudentEnquiry(int studentEnquiryId, String schoolCode) throws Exception {
        return studentEnquiryFormDao.deleteStudentEnquiry(studentEnquiryId, schoolCode);
    }


}
