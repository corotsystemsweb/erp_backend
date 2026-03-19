package com.sms.service;

import com.sms.model.StudentDetails;
import com.sms.model.StudentFullRequestDetails;
import com.sms.model.StudentFullResponseDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentService {
    //public boolean addImage(MultipartFile file, int studentId) throws Exception;
    //public StudentDetails getImage(int studentId) throws Exception;
    public boolean addImage(MultipartFile file, String schoolCode, int studentId) throws Exception;
    public StudentDetails getImage(String schoolCode, int studentId) throws Exception;
    public StudentDetails addStudentPersonalDetails(StudentDetails studentDetails, String schoolCode) throws Exception;
    public StudentDetails addStudentAcademicDetails(StudentDetails studentDetails, String schoolCode) throws Exception;
    StudentFullResponseDetails addFullStudentData(StudentFullRequestDetails req, String schoolCode, Map<String, byte[]> parentImages, MultipartFile studentImage) throws Exception;
    //public StudentDetails getStudentDetailsById(int studentId, String schoolCode) throws Exception;
    public StudentFullResponseDetails getStudentDetailsById(int studentId, String schoolCode) throws Exception;
    //public List<StudentDetails> getAllStudentDetails(int sessionId,String schoolCode) throws Exception;
    public List<StudentFullResponseDetails> getAllStudentDetails(int sessionId, String schoolCode) throws Exception;
    public StudentDetails updateStudentPersonalDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception;
    StudentFullResponseDetails updateFullStudentData(StudentFullRequestDetails req, String schoolCode, Map<String, byte[]> parentImages, MultipartFile studentImage) throws Exception;
    public StudentDetails updateStudentAcademicDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception;
    public boolean softDeleteStudent(int studentId, String schoolCode) throws Exception;
    public List<StudentDetails> searchStudentByClassNameAndSection(String studentClass, String studentSection, String schoolCode) throws Exception;
    public List<StudentDetails> searchByClassSectionAndSession(int studentClass, int studentSection, int sessionId, String schoolCode) throws Exception;
    public int getTotalStudent(String schoolCode) throws Exception;
    /////////////////////////////////////
    //List<StudentDetails> processExcelFileForStudentPersonalDetails(MultipartFile file, String schoolCode) throws Exception;
    public List<StudentDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception;
    public boolean checkRegistrationNumberExists(String registrationNumber, String schoolCode);
    public List<StudentDetails> processExcelFileForStudentPersonalDetails(MultipartFile file, String schoolCode) throws Exception;
    List<StudentDetails> processExcelFileForStudentAcademicDetails(MultipartFile file, String schoolCode) throws Exception;
    public List<StudentDetails> getStudentDetailsByParentId(int parentId, String schoolCode) throws Exception;
    boolean isEligibleForTC(Long studentId, String schoolCode);
    public StudentDetails getStudentDetailsForTc(int studentId, int sessionId,String schoolCode) throws Exception;
    public List<StudentDetails> getBirthday(String schoolCode) throws Exception;
    public List<StudentDetails> globalSearch(String firstName,String lastName,String FatherName,String AdmissionNumber,String phoneNumber,String rollNumber,int sessionId,String schoolCode) throws Exception;
}
