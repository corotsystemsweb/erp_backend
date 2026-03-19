package com.sms.service.impl;

import com.sms.dao.StudentDao;
import com.sms.dao.StudentSearchTextDao;
import com.sms.model.StudentDetails;
import com.sms.model.StudentSearchTextDetails;
import com.sms.service.StudentSearchTextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StudentSearchTextServiceImpl implements StudentSearchTextService {
    @Autowired
    private StudentSearchTextDao studentSearchTextDao;
    @Autowired
    private StudentDao studentDao;

    @Override
    public List<StudentSearchTextDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        List<StudentSearchTextDetails> studentSearchTexDetailsList =  studentSearchTextDao.getStudentDetailsBySearchText(searchText, schoolCode);

        if(studentSearchTexDetailsList != null && !studentSearchTexDetailsList.isEmpty()){
            for(StudentSearchTextDetails sst : studentSearchTexDetailsList){
                try{
                    StudentDetails studentDetailsImage = studentDao.getImage(schoolCode, sst.getStudentId());
                    if(studentDetailsImage != null){
                        sst.setStudentImage(studentDetailsImage.getStudentImage());
                    }
                }catch (IOException e){
                    // If the image is not found, simply ignore and return employee details without image
                    //Do not throw exception, just skip setting the image
                    //Logging the error is optional
                    //e.printStackTrace(); // optional: Only If I want to log
                }
            }
        }
        return studentSearchTexDetailsList;
    }

    @Override
    public List<StudentSearchTextDetails> getStudentFeeDetailsBasedOnClassSectionSessionForSearchText(int classId, int sectionId, int sessionId, String searchText, String schoolCode) throws Exception {
        List<StudentSearchTextDetails> studentSearchTexDetailsList = studentSearchTextDao.getStudentFeeDetailsBasedOnClassSectionSessionForSearchText(classId, sectionId, sessionId, searchText, schoolCode);
        if(studentSearchTexDetailsList != null && !studentSearchTexDetailsList.isEmpty()){
            for(StudentSearchTextDetails sst : studentSearchTexDetailsList){
                try{
                    StudentDetails studentDetailsImage = studentDao.getImage(schoolCode, sst.getStudentId());
                    if(studentDetailsImage != null){
                        sst.setStudentImage(studentDetailsImage.getStudentImage());
                    }
                }catch (IOException e){
                    // If the image is not found, simply ignore and return employee details without image
                    //Do not throw exception, just skip setting the image
                    //Logging the error is optional
                    //e.printStackTrace(); // optional: Only If I want to log
                }
            }
        }
        return studentSearchTexDetailsList;
    }

   /* @Override
    public List<StudentSearchTextDetails> getFeeDatilsBasedOnSession(int sessionId, String searchText, String schoolCode) throws Exception {
        List<StudentSearchTextDetails> studentSearchTexDetailsList = studentSearchTextDao.getFeeDatilsBasedOnSession(sessionId,searchText,schoolCode);
        if(studentSearchTexDetailsList != null && !studentSearchTexDetailsList.isEmpty()){
            for(StudentSearchTextDetails sst : studentSearchTexDetailsList){
                try{
                    StudentDetails studentDetailsImage = studentDao.getImage(schoolCode, sst.getStudentId());
                    if(studentDetailsImage != null){
                        sst.setStudentImage(studentDetailsImage.getStudentImage());
                    }
                }catch (IOException e){
                    // If the image is not found, simply ignore and return employee details without image
                    //Do not throw exception, just skip setting the image
                    //Logging the error is optional
                    //e.printStackTrace(); // optional: Only If I want to log
                }
            }
        }
        return studentSearchTexDetailsList;
    }*/
   @Override
   public List<StudentSearchTextDetails> getFeeDatilsBasedOnSession(int sessionId, String searchText, String schoolCode) throws Exception {
       List<StudentSearchTextDetails> studentDetailsList = studentSearchTextDao.getFeeDatilsBasedOnSession(sessionId, searchText, schoolCode);
       if (studentDetailsList == null || studentDetailsList.isEmpty()) {
           return Collections.emptyList();
       }

       List<Integer> studentIds = studentDetailsList.stream()
               .map(StudentSearchTextDetails::getStudentId)
               .collect(Collectors.toList());

       Map<Integer, StudentDetails> imageMap = studentDao.getStudentImagesBatch(schoolCode, studentIds);

       studentDetailsList.forEach(sst ->
               Optional.ofNullable(imageMap.get(sst.getStudentId()))
                       .ifPresent(img -> sst.setStudentImage(img.getStudentImage()))
       );

       return studentDetailsList;
   }
}
