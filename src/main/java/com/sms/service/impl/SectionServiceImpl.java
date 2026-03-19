package com.sms.service.impl;

import com.sms.dao.SectionDao;
import com.sms.model.SectionDetails;
import com.sms.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionServiceImpl implements SectionService {
    @Autowired
    private SectionDao sectionDao;
    @Override
    public SectionDetails addSection(SectionDetails sectionDetails,String schoolCode) throws Exception {
        return sectionDao.addSection(sectionDetails,schoolCode);
    }

    @Override
    public SectionDetails getSectionDetailsById(int sectionId, String schoolCode) throws Exception {
        return sectionDao.getSectionDetailsById(sectionId,schoolCode);
    }

    @Override
    public List<SectionDetails> getAllSectionDetails(String schoolCode) throws Exception {
        return sectionDao.getAllSectionDetails(schoolCode);
    }

    @Override
    public SectionDetails updateSectionDetailsById(SectionDetails sectionDetails, int sectionId, String schoolCode) throws Exception {
        return sectionDao.updateSectionDetailsById(sectionDetails, sectionId, schoolCode);
    }

    @Override
    public boolean deleteSection(int sectionId, String SchoolCode) throws Exception {
        return sectionDao.deleteSection(sectionId,SchoolCode);
    }
}
