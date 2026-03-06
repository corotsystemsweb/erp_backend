package com.sms.service.impl;

import com.sms.dao.StudentDao;
import com.sms.dao.StudentTransportDetailsDao;
import com.sms.dao.impl.MasterSequenceDetailsDaoImpl;
import com.sms.model.*;
import com.sms.service.ParentDetailsService;
import com.sms.service.StudentService;
import com.sms.service.TransportFeeDueService;
import com.sms.util.ExcelHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private  StudentDao studentDao;
    @Autowired
    private ParentDetailsService parentDetailsService;
    @Autowired
    private MasterSequenceDetailsDaoImpl masterSequenceDetailsDao;
    @Autowired
    private StudentTransportDetailsDao studentTransportDetailsDao;
    @Autowired
    private TransportFeeDueService transportFeeDueService;

    /*@Override
    public boolean addImage(MultipartFile file, int studentId) throws Exception {
        return studentDao.addImage(file,studentId);
    }
    @Override
    public StudentDetails getImage(int studentId) throws Exception {
        return studentDao.getImage(studentId);
    }*/
    @Override
    public boolean addImage(MultipartFile file, String schoolCode, int studentId) throws Exception{
        return studentDao.addImage(file,schoolCode,studentId);
    }
    @Override
    public StudentDetails getImage(String schoolCode, int studentId) throws Exception{
        return studentDao.getImage(schoolCode, studentId);
    }
    @Override
    public StudentDetails addStudentPersonalDetails(StudentDetails studentDetails, String schoolCode) throws Exception {
        return studentDao.addStudentPersonalDetails(studentDetails, schoolCode);
    }

    @Override
    public StudentDetails addStudentAcademicDetails(StudentDetails studentDetails, String schoolCode) throws Exception {
        return studentDao.addStudentAcademicDetails(studentDetails, schoolCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentFullResponseDetails addFullStudentData(StudentFullRequestDetails req, String schoolCode, Map<String, byte[]> parentImages, MultipartFile studentImage) throws Exception {

        //Parent list
        List<ParentDetails> parents = req.getParentDetails();
        if (parents == null || parents.isEmpty())
            throw new IllegalArgumentException("At least one parent required");

        String uuid = parents.get(0).getUuid();
        // --------------------------------

        //Student personal details
        StudentDetails personalReq = req.getStudentPersonalDetails();
        if (personalReq == null)
            throw new IllegalArgumentException("studentPersonalDetails required");

        // Assign PARENT UUID → STUDENT
        personalReq.setUuId(uuid);

        //Save parents (with images)
        List<ParentDetails> savedParents = parentDetailsService.addBulkParentDetails(parents, parentImages, schoolCode);

        // Extract parent IDs
        List<Integer> parentIds = savedParents.stream()
                .map(ParentDetails::getParentId)
                .toList();

        //Save student personal (DAO generates studentId)
        personalReq.setParentId(parentIds);

        StudentDetails savedPersonal = studentDao.addStudentPersonalDetails(personalReq, schoolCode);

        int studentId = savedPersonal.getStudentId();
        //Save student transport details
        StudentTransportDetails  transportReq = req.getStudentTransportDetails();
        if (transportReq != null && transportReq.getRouteId() != null) {

            transportReq.setStudentId(studentId);
            transportReq.setSchoolId(personalReq.getSchoolId());

            StudentTransportDetails savedTransport = studentTransportDetailsDao.addStudentTransportDetails(transportReq, schoolCode);
            // savedTransport available if needed later

            //TRANSPORT FEE DUE
            List<TransportFeeDue> feeDueList = req.getTransportFeeDue();
            if (feeDueList != null && !feeDueList.isEmpty()) {

                for (TransportFeeDue feeDue : feeDueList) {
                    feeDue.setSchoolId(personalReq.getSchoolId());
                    feeDue.setStudentTransportId(savedTransport.getStudentTransportId());
                }

                transportFeeDueService.addBulkTransportFeeDue(feeDueList, schoolCode);
            }
        }

        //Save student academic using SAME UUID
        StudentDetails academicReq = req.getStudentAcademicDetails();
        StudentDetails savedAcademic = null;

        if (academicReq != null) {
            academicReq.setStudentId(studentId);
            academicReq.setSchoolId(personalReq.getSchoolId());
            academicReq.setUuId(uuid);   // UUID inherited from parents

            savedAcademic = studentDao.addStudentAcademicDetails(academicReq, schoolCode);
        }

        //Save student image last
        if (studentImage != null && !studentImage.isEmpty()) {
            studentDao.addImage(studentImage, schoolCode, studentId);
            StudentDetails imgDetails = studentDao.getImage(schoolCode, savedPersonal.getStudentId());
            savedPersonal.setStudentImage(imgDetails.getStudentImage());
        }
        int nextSeqCode = masterSequenceDetailsDao.findNextAvailableSeqCode(schoolCode);
        int nextCurrentValue = masterSequenceDetailsDao.findNextAvailableCurrentValue(schoolCode);
        masterSequenceDetailsDao.updateSeqCodeAndCurrentValue(nextSeqCode, nextCurrentValue, schoolCode);

        //Build final response
        StudentFullResponseDetails response = new StudentFullResponseDetails();
        response.setParentDetails(savedParents);
        response.setStudentPersonalDetails(savedPersonal);
        response.setStudentAcademicDetails(savedAcademic);
        response.setStudentTransportDetails(transportReq);
        response.setTransportFeeDue(req.getTransportFeeDue());

        return response;
    }

//    @Override
//    public StudentDetails getStudentDetailsById(int studentId, String schoolCode) throws Exception {
//        // 1. Fetch student + parent basic details from DB
//        StudentDetails studentDetails = studentDao.getStudentDetailsById(studentId, schoolCode);
//        if (studentDetails == null) {
//            return null;
//        }
//        try {
//            StudentDetails img = studentDao.getImage(schoolCode, studentId);
//            if (img != null && img.getStudentImage() != null) {
//                studentDetails.setStudentImage(img.getStudentImage());
//            }
//        } catch (IOException e) {
//            // ignore if photo not available
//        }
//
//        List<ParentDetails> parents = studentDetails.getParentDetails();
//
//        if (parents != null) {
//            for (ParentDetails parent : parents) {
//
//                Map<String, String> img = parentDetailsService.loadImage(schoolCode, parent.getParentId(), parent.getParentType());
//
//                if (!img.isEmpty()) {
//                    if (parent.getParentImages() == null) {
//                        parent.setParentImages(new HashMap<>());
//                    }
//                    parent.getParentImages().putAll(img);
//                }
//            }
//        }
//
//        return studentDetails;
//    }
@Override
public StudentFullResponseDetails getStudentDetailsById(int studentId, String schoolCode) throws Exception {
    // 1) fetch combined DTO from DAO (your existing DAO method that runs the big SQL)
    StudentDetails combined = studentDao.getStudentDetailsById(studentId, schoolCode);
    if (combined == null) return null;

    // 2) try to load student image (same as you had before)
    try {
        StudentDetails img = studentDao.getImage(schoolCode, studentId);
        if (img != null && img.getStudentImage() != null) {
            combined.setStudentImage(img.getStudentImage());
        }
    } catch (IOException ignored) { }

    // 3) load parent images (if you have this helper)
    if (combined.getParentDetails() != null) {
        for (ParentDetails parent : combined.getParentDetails()) {
            Map<String, String> pImg = parentDetailsService.loadImage(schoolCode, parent.getParentId(), parent.getParentType());
            if (pImg != null && !pImg.isEmpty()) {
                if (parent.getParentImages() == null) parent.setParentImages(new HashMap<>());
                parent.getParentImages().putAll(pImg);
            }
        }
    }

    // 4) create segregated objects (minimal changes — fields set exactly as you showed)
    StudentDetails personal = new StudentDetails();
    // personal fields (as you specified)
    personal.setId(combined.getId());                                  // student_personal_id
    personal.setStudentId(combined.getStudentId());
    personal.setSchoolId(combined.getSchoolId());                      // personal_school_id
    personal.setUuId(combined.getUuId());                              // personal_uu_id
    personal.setFirstName(combined.getFirstName());
    personal.setLastName(combined.getLastName());
    personal.setBloodGroup(combined.getBloodGroup());
    personal.setGender(combined.getGender());
    personal.setHeight(combined.getHeight());
    personal.setWeight(combined.getWeight());
    personal.setAadharNumber(combined.getAadharNumber());
    personal.setPhoneNumber(combined.getPhoneNumber());
    personal.setEmergencyPhoneNumber(combined.getEmergencyPhoneNumber());
    personal.setWhatsAppNumber(combined.getWhatsAppNumber());
    personal.setEmailAddress(combined.getEmailAddress());
    personal.setDob(combined.getDob());
    personal.setDobCirtificateNo(combined.getDobCirtificateNo());
    personal.setIncomeAppNo(combined.getIncomeAppNo());
    personal.setCasteAppNo(combined.getCasteAppNo());
    personal.setDomicileAppNo(combined.getDomicileAppNo());
    personal.setGovtStudentIdOnPortal(combined.getGovtStudentIdOnPortal());
    personal.setGovtFamilyIdOnPortal(combined.getGovtFamilyIdOnPortal());
    personal.setBankName(combined.getBankName());
    personal.setBranchName(combined.getBranchName());
    personal.setIfscCode(combined.getIfscCode());
    personal.setAccountNumber(combined.getAccountNumber());
    personal.setPanNo(combined.getPanNo());
    personal.setReligion(combined.getReligion());
    personal.setNationality(combined.getNationality());
    personal.setCategory(combined.getCategory());
    personal.setCaste(combined.getCaste());
    personal.setCurrentAddress(combined.getCurrentAddress());
    personal.setCurrentCity(combined.getCurrentCity());
    personal.setCurrentState(combined.getCurrentState());
    personal.setCurrentZipCode(combined.getCurrentZipCode());
    personal.setPermanentAddress(combined.getPermanentAddress());
    personal.setPermanentCity(combined.getPermanentCity());
    personal.setPermanentState(combined.getPermanentState());
    personal.setPermanentZipCode(combined.getPermanentZipCode());
    personal.setStudentCountry(combined.getStudentCountry());
    personal.setCurrentStatus(combined.getCurrentStatus());
    personal.setCurrentStatusComment(combined.getCurrentStatusComment());
    personal.setUpdatedBy(combined.getUpdatedBy());
    personal.setUpdatedDate(combined.getUpdatedDate());
    personal.setCreateDate(combined.getCreateDate());
    personal.setValidityStartDate(combined.getValidityStartDate());
    personal.setValidityEndDate(combined.getValidityEndDate());
    personal.setStudentPhoto(combined.getStudentPhoto());
    personal.setStudentImage(combined.getStudentImage()); // keep image in personal

    // 5) academic object (fields exactly as you specified)
    StudentDetails academic = new StudentDetails();
    academic.setApaarId(combined.getApaarId());
    academic.setPenNo(combined.getPenNo());
    academic.setAdmissionNo(combined.getAdmissionNo());
    academic.setAdmissionDate(combined.getAdmissionDate());
    academic.setRegistrationNumber(combined.getRegistrationNumber());
    academic.setRollNumber(combined.getRollNumber());
    academic.setSessionId(combined.getSessionId());
    academic.setStudentClassId(combined.getStudentClassId());
    academic.setStudentSectionId(combined.getStudentSectionId());
    academic.setStream(combined.getStream());
    academic.setEducationMedium(combined.getEducationMedium());
    academic.setReferredBy(combined.getReferredBy());
    academic.setRteStudent(combined.isRteStudent());
    academic.setRteApplicationNo(combined.getRteApplicationNo());
    academic.setEnrolledSession(combined.getEnrolledSession());
    academic.setEnrolledClass(combined.getEnrolledClass());
    academic.setEnrolledYear(combined.getEnrolledYear());
    academic.setTransferCirtiNo(combined.getTransferCirtiNo());
    academic.setDateOfIssue(combined.getDateOfIssue());
    academic.setScholarshipId(combined.getScholarshipId());
    academic.setScholarshipPassword(combined.getScholarshipPassword());
    academic.setLstSchoolName(combined.getLstSchoolName());
    academic.setLstSchoolAddress(combined.getLstSchoolAddress());
    academic.setLstAttendedClass(combined.getLstAttendedClass());
    academic.setLstSclAffTo(combined.getLstSclAffTo());
    academic.setLstSession(combined.getLstSession());
    academic.setDropOut(combined.isDropOut());
    academic.setDropOutDate(combined.getDropOutDate());
    academic.setDropOutReason(combined.getDropOutReason());
    academic.setStudentAdmissionType(combined.getStudentAdmissionType());
    academic.setSessionStatus(combined.getSessionStatus());
    academic.setSessionStatusComment(combined.getSessionStatusComment());

    // previous qualifications (JSON -> list) copy from combined
    academic.setPreviousQualificationDetails(combined.getPreviousQualificationDetails());

    // class/section/session/school info belongs to academic portion (as per your mapping)
    academic.setStudentType(combined.getStudentType());
    academic.setClassId(combined.getClassId());
    academic.setClassName(combined.getClassName());
    academic.setSectionId(combined.getSectionId());
    academic.setSectionName(combined.getSectionName());
    academic.setAcademicSession(combined.getAcademicSession());
    academic.setSchoolName(combined.getSchoolName());
    academic.setSchoolAddress(combined.getSchoolAddress());
    academic.setSchoolCity(combined.getSchoolCity());
    academic.setSchoolState(combined.getSchoolState());
    academic.setSchoolCountry(combined.getSchoolCountry());
    academic.setSchoolZipCode(combined.getSchoolZipCode());
    academic.setSchoolEmailAddress(combined.getSchoolEmailAddress());
    academic.setSchoolPhoneNumber(combined.getSchoolPhoneNumber());

    // keep common linking ids where useful
    personal.setStudentId(combined.getStudentId());
    academic.setStudentId(combined.getStudentId());

    // 6) parents go to top-level parentDetails
    List<ParentDetails> parents = combined.getParentDetails();

    // 7) prepare final response
    StudentFullResponseDetails response = new StudentFullResponseDetails();
    response.setParentDetails(parents);
    response.setStudentPersonalDetails(personal);
    response.setStudentAcademicDetails(academic);

    return response;
}


//    @Override
//    public List<StudentDetails> getAllStudentDetails(int sessionId, String schoolCode) throws Exception {
//        List<StudentDetails> studentDetailsList = studentDao.getAllStudentDetails(sessionId, schoolCode);
//        if(studentDetailsList == null || studentDetailsList.isEmpty()){
//            return studentDetailsList;
//        }
//        for(StudentDetails student : studentDetailsList){
//            int studentId = student.getStudentId();
//
//            try{
//                StudentDetails img = studentDao.getImage(schoolCode, studentId);
//                if(img != null && img.getStudentImage() != null){
//                    student.setStudentImage(img.getStudentImage());
//                }
//            } catch(Exception e){
//                // ignore if images are not present
//            }
//            //Add parents images
//            List<ParentDetails> parents = student.getParentDetails();
//
//            if(parents != null){
//                for(ParentDetails parent : parents){
//                    Map<String, String> pImg = parentDetailsService.loadImage(schoolCode, parent.getParentId(), parent.getParentType());
//                    if(parent.getParentImages() == null){
//                        parent.setParentImages(new HashMap<>());
//                    }
//                    parent.getParentImages().putAll(pImg);
//                }
//            }
//        }
//        return studentDetailsList;
//    }

    @Override
    public List<StudentFullResponseDetails> getAllStudentDetails(int sessionId, String schoolCode) throws Exception {
        List<StudentDetails> list = studentDao.getAllStudentDetails(sessionId, schoolCode);

        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }

        List<StudentFullResponseDetails> finalList = new ArrayList<>();

        for (StudentDetails combined : list) {

            int studentId = combined.getStudentId();

            // Load student photo
            try {
                StudentDetails img = studentDao.getImage(schoolCode, studentId);
                if (img != null && img.getStudentImage() != null) {
                    combined.setStudentImage(img.getStudentImage());
                }
            } catch (Exception ignored) {}

            // Load parent images
            if (combined.getParentDetails() != null) {
                for (ParentDetails parent : combined.getParentDetails()) {
                    Map<String, String> pImg =
                            parentDetailsService.loadImage(schoolCode, parent.getParentId(), parent.getParentType());

                    if (pImg != null && !pImg.isEmpty()) {
                        if (parent.getParentImages() == null) parent.setParentImages(new HashMap<>());
                        parent.getParentImages().putAll(pImg);
                    }
                }
            }

            // SEGREGATE PERSONAL FIELDS
            StudentDetails personal = new StudentDetails();
            personal.setId(combined.getId());
            personal.setStudentId(combined.getStudentId());
            personal.setSchoolId(combined.getSchoolId());
            personal.setUuId(combined.getUuId());
            personal.setFirstName(combined.getFirstName());
            personal.setLastName(combined.getLastName());
            personal.setBloodGroup(combined.getBloodGroup());
            personal.setGender(combined.getGender());
            personal.setHeight(combined.getHeight());
            personal.setWeight(combined.getWeight());
            personal.setAadharNumber(combined.getAadharNumber());
            personal.setPhoneNumber(combined.getPhoneNumber());
            personal.setEmergencyPhoneNumber(combined.getEmergencyPhoneNumber());
            personal.setWhatsAppNumber(combined.getWhatsAppNumber());
            personal.setEmailAddress(combined.getEmailAddress());
            personal.setDob(combined.getDob());
            personal.setDobCirtificateNo(combined.getDobCirtificateNo());
            personal.setIncomeAppNo(combined.getIncomeAppNo());
            personal.setCasteAppNo(combined.getCasteAppNo());
            personal.setDomicileAppNo(combined.getDomicileAppNo());
            personal.setGovtStudentIdOnPortal(combined.getGovtStudentIdOnPortal());
            personal.setGovtFamilyIdOnPortal(combined.getGovtFamilyIdOnPortal());
            personal.setBankName(combined.getBankName());
            personal.setBranchName(combined.getBranchName());
            personal.setIfscCode(combined.getIfscCode());
            personal.setAccountNumber(combined.getAccountNumber());
            personal.setPanNo(combined.getPanNo());
            personal.setReligion(combined.getReligion());
            personal.setNationality(combined.getNationality());
            personal.setCategory(combined.getCategory());
            personal.setCaste(combined.getCaste());
            personal.setCurrentAddress(combined.getCurrentAddress());
            personal.setCurrentCity(combined.getCurrentCity());
            personal.setCurrentState(combined.getCurrentState());
            personal.setCurrentZipCode(combined.getCurrentZipCode());
            personal.setPermanentAddress(combined.getPermanentAddress());
            personal.setPermanentCity(combined.getPermanentCity());
            personal.setPermanentState(combined.getPermanentState());
            personal.setPermanentZipCode(combined.getPermanentZipCode());
            personal.setStudentCountry(combined.getStudentCountry());
            personal.setCurrentStatus(combined.getCurrentStatus());
            personal.setCurrentStatusComment(combined.getCurrentStatusComment());
            personal.setUpdatedBy(combined.getUpdatedBy());
            personal.setUpdatedDate(combined.getUpdatedDate());
            personal.setCreateDate(combined.getCreateDate());
            personal.setValidityStartDate(combined.getValidityStartDate());
            personal.setValidityEndDate(combined.getValidityEndDate());
            personal.setStudentPhoto(combined.getStudentPhoto());
            personal.setStudentImage(combined.getStudentImage());

            // SEGREGATE ACADEMIC FIELDS
            StudentDetails academic = new StudentDetails();
            academic.setApaarId(combined.getApaarId());
            academic.setPenNo(combined.getPenNo());
            academic.setAdmissionNo(combined.getAdmissionNo());
            academic.setAdmissionDate(combined.getAdmissionDate());
            academic.setRegistrationNumber(combined.getRegistrationNumber());
            academic.setRollNumber(combined.getRollNumber());
            academic.setSessionId(combined.getSessionId());
            academic.setStudentClassId(combined.getStudentClassId());
            academic.setStudentSectionId(combined.getStudentSectionId());
            academic.setStream(combined.getStream());
            academic.setEducationMedium(combined.getEducationMedium());
            academic.setReferredBy(combined.getReferredBy());
            academic.setRteStudent(combined.isRteStudent());
            academic.setRteApplicationNo(combined.getRteApplicationNo());
            academic.setEnrolledSession(combined.getEnrolledSession());
            academic.setEnrolledClass(combined.getEnrolledClass());
            academic.setEnrolledYear(combined.getEnrolledYear());
            academic.setTransferCirtiNo(combined.getTransferCirtiNo());
            academic.setDateOfIssue(combined.getDateOfIssue());
            academic.setScholarshipId(combined.getScholarshipId());
            academic.setScholarshipPassword(combined.getScholarshipPassword());
            academic.setLstSchoolName(combined.getLstSchoolName());
            academic.setLstSchoolAddress(combined.getLstSchoolAddress());
            academic.setLstAttendedClass(combined.getLstAttendedClass());
            academic.setLstSclAffTo(combined.getLstSclAffTo());
            academic.setLstSession(combined.getLstSession());
            academic.setDropOut(combined.isDropOut());
            academic.setDropOutDate(combined.getDropOutDate());
            academic.setDropOutReason(combined.getDropOutReason());
            academic.setStudentAdmissionType(combined.getStudentAdmissionType());
            academic.setSessionStatus(combined.getSessionStatus());
            academic.setSessionStatusComment(combined.getSessionStatusComment());
            academic.setPreviousQualificationDetails(combined.getPreviousQualificationDetails());

            academic.setStudentType(combined.getStudentType());
            academic.setClassId(combined.getClassId());
            academic.setClassName(combined.getClassName());
            academic.setSectionId(combined.getSectionId());
            academic.setSectionName(combined.getSectionName());
            academic.setAcademicSession(combined.getAcademicSession());
            academic.setSchoolName(combined.getSchoolName());
            academic.setSchoolAddress(combined.getSchoolAddress());
            academic.setSchoolCity(combined.getSchoolCity());
            academic.setSchoolState(combined.getSchoolState());
            academic.setSchoolCountry(combined.getSchoolCountry());
            academic.setSchoolZipCode(combined.getSchoolZipCode());
            academic.setSchoolEmailAddress(combined.getSchoolEmailAddress());
            academic.setSchoolPhoneNumber(combined.getSchoolPhoneNumber());

            // BUILD FINAL RESPONSE OBJECT
            StudentFullResponseDetails res = new StudentFullResponseDetails();
            res.setStudentPersonalDetails(personal);
            res.setStudentAcademicDetails(academic);
            res.setParentDetails(combined.getParentDetails());

            finalList.add(res);
        }

        return finalList;
    }


    @Override
    public StudentDetails updateStudentPersonalDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception {
        return studentDao.updateStudentPersonalDetails(studentDetails, studentId, schoolCode);
    }

//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public StudentFullResponseDetails updateFullStudentData(StudentFullRequestDetails req, String schoolCode, Map<String, byte[]> parentImages, MultipartFile studentImage) throws Exception {
//        List<ParentDetails> parents = req.getParentDetails();
//        if (parents == null || parents.isEmpty())
//            throw new IllegalArgumentException("At least one parent required");
//
//        StudentDetails personalReq = req.getStudentPersonalDetails();
//        if (personalReq == null)
//            throw new IllegalArgumentException("studentPersonalDetails required");
//
//        StudentDetails academicReq = req.getStudentAcademicDetails();
//        if (academicReq == null)
//            throw new IllegalArgumentException("studentAcademicDetails required");
//
//        int studentId = personalReq.getStudentId();
//        if (studentId <= 0)
//            throw new IllegalArgumentException("studentId required for update");
//
//        String uuid = personalReq.getUuId();
//        if (uuid == null || uuid.isBlank())
//            throw new IllegalArgumentException("UUID missing for update");
//
//        // 1 UPDATE PARENTS
//        List<ParentDetails> updatedParents = parentDetailsService.updateBulkParentDetailsById(parents, parentImages, schoolCode);
//
//        List<Integer> parentIds = updatedParents.stream()
//                .map(ParentDetails::getParentId)
//                .toList();
//
//
//        // 2 UPDATE STUDENT PERSONAL DETAILS
//        personalReq.setParentId(parentIds);
//        personalReq.setUuId(uuid);
//
//        StudentDetails updatedPersonal = studentDao.updateStudentPersonalDetails(personalReq, studentId, schoolCode);
//
//
//        // 3 UPDATE STUDENT ACADEMIC DETAILS
//        academicReq.setStudentId(studentId);
//        academicReq.setUuId(uuid);
//
//        StudentDetails updatedAcademic = studentDao.updateStudentAcademicDetails(academicReq, studentId, schoolCode);
//
//
//        // 4 UPDATE STUDENT IMAGE
//        if (studentImage != null && !studentImage.isEmpty()) {
//            studentDao.addImage(studentImage, schoolCode, studentId);
//
//            StudentDetails img = studentDao.getImage(schoolCode, studentId);
//
//            if (img != null && img.getStudentImage() != null)
//                updatedPersonal.setStudentImage(img.getStudentImage());
//        }
//
//
//        // 5 ATTACH UPDATED PARENT IMAGES
//        for (ParentDetails p : updatedParents) {
//
//            Map<String, String> img = parentDetailsService.loadImage(schoolCode, p.getParentId(), p.getParentType());
//
//            if (p.getParentImages() == null)
//                p.setParentImages(new HashMap<>());
//
//            p.getParentImages().putAll(img);
//        }
//
//
//        // 6 BUILD FINAL RESPONSE
//        StudentFullResponseDetails response = new StudentFullResponseDetails();
//        response.setParentDetails(updatedParents);
//        response.setStudentPersonalDetails(updatedPersonal);
//        response.setStudentAcademicDetails(updatedAcademic);
//
//        return response;
//    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentFullResponseDetails updateFullStudentData(StudentFullRequestDetails req, String schoolCode, Map<String, byte[]> parentImages, MultipartFile studentImage) throws Exception {

        List<ParentDetails> parents = req.getParentDetails();
        if (parents == null || parents.isEmpty())
            throw new IllegalArgumentException("At least one parent required");

        StudentDetails personalReq = req.getStudentPersonalDetails();
        if (personalReq == null)
            throw new IllegalArgumentException("studentPersonalDetails required");

        StudentDetails academicReq = req.getStudentAcademicDetails();
        if (academicReq == null)
            throw new IllegalArgumentException("studentAcademicDetails required");

        int studentId = personalReq.getStudentId();
        if (studentId <= 0)
            throw new IllegalArgumentException("studentId required for update");

        String uuid = personalReq.getUuId();
        if (uuid == null || uuid.isBlank())
            throw new IllegalArgumentException("UUID missing for update");

       //1. UPDATE + ADD PARENTS
        List<ParentDetails> existingParents = new ArrayList<>();
        List<ParentDetails> newParents = new ArrayList<>();

        for (ParentDetails p : parents) {
            p.setUuid(uuid); // IMPORTANT — keep UUID consistency

            if (p.getParentId() != null && p.getParentId() > 0) {
                existingParents.add(p);
            } else {
                newParents.add(p);
            }
        }

        List<ParentDetails> updatedParents = existingParents.isEmpty() ? new ArrayList<>() : parentDetailsService.updateBulkParentDetailsById(existingParents, parentImages, schoolCode);

        List<ParentDetails> insertedParents = newParents.isEmpty() ? new ArrayList<>() : parentDetailsService.addBulkParentDetails(newParents, parentImages, schoolCode);

//        List<ParentDetails> allParents = new ArrayList<>();
//        allParents.addAll(updatedParents);
//        allParents.addAll(insertedParents);

        Map<Integer, ParentDetails> uniqueParents = new LinkedHashMap<>();

        for (ParentDetails p : updatedParents) {
            uniqueParents.put(p.getParentId(), p);
        }
        for (ParentDetails p : insertedParents) {
            uniqueParents.put(p.getParentId(), p);
        }

        List<ParentDetails> allParents = new ArrayList<>(uniqueParents.values());


        List<Integer> parentIds = allParents.stream()
                .map(ParentDetails::getParentId)
                .distinct()
                .toList();

       //2. UPDATE STUDENT PERSONAL
        personalReq.setParentId(parentIds);
        personalReq.setUuId(uuid);

        StudentDetails updatedPersonal = studentDao.updateStudentPersonalDetails(personalReq, studentId, schoolCode);

       //3. UPDATE STUDENT ACADEMIC
        academicReq.setStudentId(studentId);
        academicReq.setUuId(uuid);

        StudentDetails updatedAcademic = studentDao.updateStudentAcademicDetails(academicReq, studentId, schoolCode);

       //4. UPDATE STUDENT IMAGE
        if (studentImage != null && !studentImage.isEmpty()) {
            studentDao.addImage(studentImage, schoolCode, studentId);

            StudentDetails img = studentDao.getImage(schoolCode, studentId);
            if (img != null && img.getStudentImage() != null)
                updatedPersonal.setStudentImage(img.getStudentImage());
        }

       //5. ATTACH PARENT IMAGES
        for (ParentDetails p : allParents) {
            Map<String, String> img =
                    parentDetailsService.loadImage(schoolCode, p.getParentId(), p.getParentType());

            if (p.getParentImages() == null)
                p.setParentImages(new HashMap<>());

            p.getParentImages().putAll(img);
        }

       //6. BUILD RESPONSE
        StudentFullResponseDetails response = new StudentFullResponseDetails();
        response.setParentDetails(allParents);
        response.setStudentPersonalDetails(updatedPersonal);
        response.setStudentAcademicDetails(updatedAcademic);

        return response;
    }


    @Override
    public StudentDetails updateStudentAcademicDetails(StudentDetails studentDetails, int studentId, String schoolCode) throws Exception {
        return studentDao.updateStudentAcademicDetails(studentDetails, studentId, schoolCode);
    }

    @Override
    public boolean softDeleteStudent(int studentId, String schoolCode) throws Exception {
        return studentDao.softDeleteStudent(studentId, schoolCode);
    }

    @Override
    public List<StudentDetails> searchStudentByClassNameAndSection(String studentClass, String studentSection, String schoolCode) throws Exception {
        return studentDao.searchStudentByClassNameAndSection(studentClass, studentSection, schoolCode);
    }

    @Override
    public List<StudentDetails> searchByClassSectionAndSession(int studentClass, int studentSection, int sessionId, String schoolCode) throws Exception {
        return studentDao.searchByClassSectionAndSession(studentClass, studentSection, sessionId, schoolCode);
    }

    @Override
    public int getTotalStudent(String schoolCode) throws Exception {
        return studentDao.getTotalStudent(schoolCode);
    }

    @Override
    public List<StudentDetails> getStudentDetailsBySearchText(String searchText, String schoolCode) throws Exception {
        return studentDao.getStudentDetailsBySearchText(searchText, schoolCode);
    }

    @Override
    public boolean checkRegistrationNumberExists(String registrationNumber, String schoolCode) {
        return studentDao.checkRegistrationNumberExists(registrationNumber, schoolCode);
    }
    @Override
    public List<StudentDetails> processExcelFileForStudentPersonalDetails(MultipartFile file, String schoolCode) throws Exception {
        // Fetch current seq_code and current_value from the master_sequence_controller table
        int nextSeqCode = masterSequenceDetailsDao.findNextAvailableSeqCode(schoolCode);
        int nextCurrentValue = masterSequenceDetailsDao.findNextAvailableCurrentValue(schoolCode);

        List<StudentDetails> students = ExcelHelper.convertExcelToListOfStudentPersonalDetails(file.getInputStream());

        // Prepare a list to hold updated students
        List<StudentDetails> updatedStudents = new ArrayList<>();

        // Batch insert students and update their IDs
        for (StudentDetails student : students) {
            student.setStudentId(nextCurrentValue);  // Set current value as student ID
            studentDao.addStudentPersonalDetails(student, schoolCode);  // Insert student details

            masterSequenceDetailsDao.updateSeqCodeAndCurrentValue(nextSeqCode, nextCurrentValue, schoolCode);
            // Increment seq_code and current_value for each student
            nextSeqCode++;
            nextCurrentValue++;

            // Add the updated student to the list
            updatedStudents.add(student);
        }
        return updatedStudents;
    }
   /* @Override
    public List<StudentDetails> processExcelFileForStudentAcademicDetails(MultipartFile file, String schoolCode) throws Exception {
        List<StudentDetails> students = ExcelHelper.convertExcelToListOfStudentAcademicDetails(file.getInputStream());
        students.forEach(student -> {
            try {
                studentDao.addStudentAcademicDetails(student, schoolCode);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return students;
    }*/
   @Override
   public List<StudentDetails> processExcelFileForStudentAcademicDetails(MultipartFile file, String schoolCode) throws Exception {
       List<StudentDetails> students = ExcelHelper.convertExcelToListOfStudentAcademicDetails(file.getInputStream());
       // Prepare a list to hold updated students
       List<StudentDetails> updatedStudents = new ArrayList<>();

       // Batch insert students and update their IDs
       for (StudentDetails student : students) {
           studentDao.addStudentAcademicDetailsForExcel(student, schoolCode);  // Insert student details

           updatedStudents.add(student);
       }
       return updatedStudents;
   }

    @Override
    public List<StudentDetails> getStudentDetailsByParentId(int parentId, String schoolCode) throws Exception {
        return studentDao.getStudentDetailsByParentId(parentId, schoolCode);
    }

    @Override
    public boolean isEligibleForTC(Long studentId, String schoolCode) {
        return studentDao.isEligibleForTC(studentId,schoolCode);
    }

    @Override
    public StudentDetails getStudentDetailsForTc(int studentId, int sessionId, String schoolCode) throws Exception {
        return studentDao.getStudentDetailsForTc(studentId,sessionId,schoolCode);
    }

    @Override
    public List<StudentDetails> getBirthday(String schoolCode) throws Exception {
        List<StudentDetails> birthdayStudents = studentDao.getBirthday(schoolCode);
        if (birthdayStudents != null && !birthdayStudents.isEmpty()) {
            for (StudentDetails student : birthdayStudents) {
                try {
                    // Fetch the student's image
                    StudentDetails imageDetails = studentDao.getImage(schoolCode, student.getStudentId());
                    if (imageDetails != null) {
                        student.setStudentImage(imageDetails.getStudentImage());
                    }
                } catch (IOException e) {
                    // Handle exception if image not found (optional logging)
                    // e.printStackTrace();
                }
            }
        }
        return birthdayStudents;
    }

    @Override
    public List<StudentDetails> globalSearch(String firstName, String lastName, String FatherName, String AdmissionNumber, String phoneNumber, String rollNumber, int sessionId,String schoolCode) throws Exception {
        return studentDao.globalSearch(firstName,lastName,FatherName,AdmissionNumber,phoneNumber,rollNumber,sessionId,schoolCode);
    }
}
