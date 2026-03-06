package com.sms.dao;

import com.sms.model.ClassAndSectionDetails;

import java.util.List;
import java.util.Map;

public interface ClassAndSectionDao {
    public ClassAndSectionDetails addClassAndSection(ClassAndSectionDetails classAndSectionDetails,String schoolCode) throws Exception;
    public ClassAndSectionDetails getClassAndSectionById(int classSectionId, String schoolCode) throws Exception;
    public List<ClassAndSectionDetails> getAllClassAndSection(String schoolCode) throws Exception;
    public ClassAndSectionDetails updateClassAndSection(ClassAndSectionDetails classAndSectionDetails, int classSectionId, String schoolCode) throws Exception;
    public boolean deleteClassAndSection(int classSectionId, String schoolCode) throws Exception;
    public List<ClassAndSectionDetails> getClassRelatedSection(String schoolCode) throws Exception;
}
