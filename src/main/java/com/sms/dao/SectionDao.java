package com.sms.dao;

import com.sms.model.ClassDetails;
import com.sms.model.SectionDetails;

import java.util.List;

public interface SectionDao {
    public SectionDetails addSection(SectionDetails sectionDetails,String schoolCode) throws Exception;
    public SectionDetails getSectionDetailsById(int sectionId,String schoolCode) throws Exception;
    public List<SectionDetails> getAllSectionDetails(String schoolCode) throws Exception;
    public SectionDetails updateSectionDetailsById(SectionDetails sectionDetails, int sectionId, String schoolCode) throws Exception;
    public boolean deleteSection(int sectionId,String schoolCode) throws Exception;
}
