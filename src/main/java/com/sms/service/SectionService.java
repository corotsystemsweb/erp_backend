package com.sms.service;

import com.sms.model.SectionDetails;

import java.util.List;

public interface SectionService {
    public SectionDetails addSection(SectionDetails sectionDetails, String schoolCode) throws Exception;
    public SectionDetails getSectionDetailsById(int sectionId, String schoolCode) throws Exception;
    public List<SectionDetails> getAllSectionDetails(String SchoolCode) throws Exception;
    public SectionDetails updateSectionDetailsById(SectionDetails sectionDetails, int sectionId, String schoolCode) throws Exception;
    public boolean deleteSection(int sectionId,String schoolCode) throws Exception;
}
