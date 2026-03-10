package com.sms.dao;

import com.sms.model.StudentDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface StudentDao {
    //public boolean addImage(MultipartFile file, int studentId) throws Exception;
    //public StudentDetails getImage(int studentId) throws Exception;
    public boolean addImage(MultipartFile file, String schoolCode, int studentId) throws Exception;
    public StudentDetails getImage(String schoolCode, int studentId) throws Exception;
    public StudentDetails addStudentPersonalDetails(StudentDetails studentDetails, String schoolCode) throws Exception;
    public StudentDetails addStudentAcademicDetails(StudentDetails studentDetails, String schoolCode) throws Exception;
    public StudentDetails getStudentDetailsById(int studentId, String schoolCode) throws Exception;
    public List<StudentDetails> getAllStudentDetails(int sessionId,String schoolCode) throws Exception;
    public StudentDetails updateStudentPersonalDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception;
    public StudentDetails updateStudentAcademicDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception;
    public boolean softDeleteStudent(int studentId, String schoolCode) throws Exception;
    public List<StudentDetails> searchStudentByClassNameAndSection(String studentClass, String studentSection, String schoolCode) throws Exception;
    public List<StudentDetails> searchByClassSectionAndSession(int studentClass, int studentSection, int sessionId, String schoolCode) throws Exception;
    public int getTotalStudent(String schoolCode) throws Exception;
    public List<StudentDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception;
    public boolean checkRegistrationNumberExists(String registrationNumber, String schoolCode);
    public StudentDetails addStudentAcademicDetailsForExcel(StudentDetails studentDetails, String schoolCode) throws Exception;
    public List<StudentDetails> getStudentDetailsByParentId(int parentId, String schoolCode) throws Exception;
    boolean isEligibleForTC(Long studentId, String schoolCode);
    public StudentDetails getStudentDetailsForTc(int studentId, int sessionId,String schoolCode) throws Exception;
    Map<Integer, StudentDetails> getStudentImagesBatch(String schoolCode, List<Integer> studentIds);
    public List<StudentDetails> getBirthday(String schoolCode) throws Exception;
    public List<StudentDetails> globalSearch(String firstName,String lastName,String FatherName,String AdmissionNumber,String phoneNumber,String rollNumber,int sessionId,String schoolCode) throws Exception;
}
