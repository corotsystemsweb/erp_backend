package com.sms.service;

import com.sms.model.ClassDetails;

import java.util.List;

public interface ClassService {
    public ClassDetails addClass(ClassDetails classDetails,String schoolCode) throws Exception;
    public ClassDetails getClassDetailsById(int classId,String schoolCode) throws Exception;
    public List<ClassDetails> getAllClassDetails(String schoolCode) throws Exception;
    public ClassDetails updateClassDetailsById(ClassDetails classDetails, int classId, String schoolCode) throws Exception;
    public boolean deleteClass(int classId,String schoolCode) throws Exception;
}
