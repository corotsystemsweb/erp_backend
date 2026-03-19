package com.sms.service.impl;

import com.sms.dao.ClassSubjectAllocationDao;
import com.sms.model.ClassSubjectAllocationDetails;
import com.sms.service.ClassSubjectAllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ClassSubjectAllocationServiceImpl implements ClassSubjectAllocationService {
    @Autowired
    private ClassSubjectAllocationDao classSubjectAllocationDao;

    @Override
    public List<ClassSubjectAllocationDetails> addAllocateSubject(List<ClassSubjectAllocationDetails> classSubjectAllocationDetailsList, String schoolCode) throws Exception {
        return classSubjectAllocationDao.addAllocateSubject(classSubjectAllocationDetailsList,schoolCode);
    }

    @Override
    public List<ClassSubjectAllocationDetails> getAllAlocatedSubject(String schoolCode) throws Exception {
        return classSubjectAllocationDao.getAllAlocatedSubject(schoolCode);
    }

    @Override
    public ClassSubjectAllocationDetails getAllocatedClassSubjectById(int csaId,String schoolCode) throws Exception {
        return classSubjectAllocationDao.getAllocatedClassSubjectById(csaId, schoolCode);
    }

    @Override
    public ClassSubjectAllocationDetails updateAllocatedSubject(ClassSubjectAllocationDetails classSubjectAllocationDetails, int csaId, String schoolCode) throws Exception {
        return classSubjectAllocationDao.updateAllocatedSubject(classSubjectAllocationDetails,csaId,schoolCode);
    }

    @Override
    public boolean deleteAllocatedSubject(int csaId, String schoolCode) throws Exception {
        return classSubjectAllocationDao.deleteAllocatedSubject(csaId,schoolCode);
    }

    @Override
    public List<ClassSubjectAllocationDetails> findSubject(int classId, int sectionId, int sessionId, String schoolCode) throws Exception {
        return classSubjectAllocationDao.findSubject(classId,sectionId,sessionId,schoolCode);
    }

    @Override
    public List<Map<String, Object>> getClassSectionSubjectStructure(String schoolCode) throws Exception {
        List<ClassSubjectAllocationDetails> allocations = classSubjectAllocationDao.getAllAlocatedSubject(schoolCode);

        Map<Integer, Map<String, Object>> classMap = new LinkedHashMap<>();
        Map<String, Map<String, Object>> sectionMap = new LinkedHashMap<>();

        for (ClassSubjectAllocationDetails allocation : allocations) {
            // Class level
            Map<String, Object> classEntry = classMap.computeIfAbsent(allocation.getClassId(), k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("classId", allocation.getClassId());
                entry.put("className", allocation.getClassName());
                entry.put("sections", new ArrayList<Map<String, Object>>());
                return entry;
            });

            // Section level
            String sectionKey = allocation.getClassId() + "-" + allocation.getSectionId();
            Map<String, Object> sectionEntry = sectionMap.computeIfAbsent(sectionKey, k -> {
                Map<String, Object> entry = new LinkedHashMap<>();
                entry.put("sectionId", allocation.getSectionId());
                entry.put("sectionName", allocation.getSectionName());
                entry.put("subjects", new LinkedHashSet<Map<String, Object>>());

                ((List<Map<String, Object>>) classEntry.get("sections")).add(entry);
                return entry;
            });

            // Subject level
            Map<String, Object> subjectEntry = new LinkedHashMap<>();
            subjectEntry.put("subjectId", allocation.getSubjectId());
            subjectEntry.put("subjectName", allocation.getSubjectName());

            ((Set<Map<String, Object>>) sectionEntry.get("subjects")).add(subjectEntry);
        }

        return new ArrayList<>(classMap.values());
    }

    @Override
    public void updateAllocatedSubjects(int schoolId, int sessionId, int classId, int sectionId, List<Integer> subjectIds, String schoolCode) {
        // Delete existing entries
        classSubjectAllocationDao.deleteByClassSection(schoolId, sessionId, classId, sectionId, schoolCode);

        // Insert new subjects
        if (subjectIds != null && !subjectIds.isEmpty()) {
            classSubjectAllocationDao.insertSubjects(schoolId, sessionId, classId, sectionId, subjectIds, schoolCode);
        }
    }

}
