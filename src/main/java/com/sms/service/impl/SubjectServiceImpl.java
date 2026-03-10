package com.sms.service.impl;

import com.sms.dao.SubjectDao;
import com.sms.model.SubjectDetails;
import com.sms.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class SubjectServiceImpl implements SubjectService {
    @Autowired
    private SubjectDao subjectDao;
    @Override
    public SubjectDetails addSubject(SubjectDetails subjectDetails, String schoolCode, MultipartFile syllabusFile) throws Exception {
        SubjectDetails savedSubject = subjectDao.addSubject(subjectDetails, schoolCode);

        if(savedSubject == null){
            return null;
        }
        int subjectId = savedSubject.getSubjectId();

        // Upload syllabus if present
        if (syllabusFile != null && !syllabusFile.isEmpty()){
            subjectDao.uploadSyllabus(syllabusFile, schoolCode, subjectId);
            SubjectDetails fileDetails = subjectDao.getSyllabus(schoolCode, subjectId);

            if (fileDetails != null) {
                savedSubject.setSyllabus(fileDetails.getSyllabus());
                savedSubject.setSyllabusFileName(fileDetails.getSyllabusFileName());
            }
        }
        return savedSubject;
    }

    @Override
    public SubjectDetails getSubjectDetailsById(int subjectId, String schoolCode) throws Exception {
        SubjectDetails subjectDetails = subjectDao.getSubjectDetailsById(subjectId, schoolCode);

        if (subjectDetails == null) {
            return null;
        }

        try {

            SubjectDetails fileDetails = subjectDao.getSyllabus(schoolCode, subjectId);

            if (fileDetails != null && fileDetails.getSyllabus() != null) {

                subjectDetails.setSyllabus(fileDetails.getSyllabus());
                subjectDetails.setSyllabusFileName(fileDetails.getSyllabusFileName());
            }

        } catch (Exception e) {
            // ignore if syllabus not available
        }

        return subjectDetails;
    }

    @Override
    public List<SubjectDetails> getAllSubjectDetails(String schoolCode) throws Exception {
        List<SubjectDetails> subjects = subjectDao.getAllSubjectDetails(schoolCode);

        if(subjects == null || subjects.isEmpty()){
            return subjects;
        }

        for(SubjectDetails subject : subjects){

            try{
                SubjectDetails fileDetails = subjectDao.getSyllabus(schoolCode, subject.getSubjectId());

                if(fileDetails != null && fileDetails.getSyllabus() != null){
                    subject.setSyllabus(fileDetails.getSyllabus());
                    subject.setSyllabusFileName(fileDetails.getSyllabusFileName());
                }

            }catch(Exception e){
                // ignore if syllabus not present
            }
        }

        return subjects;
    }

    @Override
    public SubjectDetails updateSubjectDetailsById(SubjectDetails subjectDetails, int subjectId, String schoolCode, MultipartFile syllabusFile) throws Exception {
        SubjectDetails updatedSubject = subjectDao.updateSubjectDetailsById(subjectDetails, subjectId, schoolCode);

        if (updatedSubject == null) {
            return null;
        }

        // Update syllabus if new file uploaded
        if (syllabusFile != null && !syllabusFile.isEmpty()) {

            subjectDao.uploadSyllabus(syllabusFile, schoolCode, subjectId);

            SubjectDetails fileDetails = subjectDao.getSyllabus(schoolCode, subjectId);

            if (fileDetails != null) {
                updatedSubject.setSyllabus(fileDetails.getSyllabus());
                updatedSubject.setSyllabusFileName(fileDetails.getSyllabusFileName());
            }
        }

        return updatedSubject;
    }

    @Override
    public boolean deleteSubject(int subjectId, String schoolCode) throws Exception {
        return subjectDao.deleteSubject(subjectId,schoolCode);
    }

}
