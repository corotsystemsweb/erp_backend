package com.sms.service.impl;

import com.sms.dao.LibraryMemberDao;
import com.sms.model.LibraryMemberDetails;
import com.sms.service.LibraryMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibraryMemberServiceImpl implements LibraryMemberService {

    @Autowired
    private LibraryMemberDao libraryMemberDao;

    @Override
    public LibraryMemberDetails addMember(LibraryMemberDetails details, String schoolCode) throws Exception {

        // 🔥 basic validation
        if (details.getMemberName() == null || details.getMemberName().isEmpty()) {
            throw new RuntimeException("Member name is required");
        }

        if (details.getMemberType().equalsIgnoreCase("Student") && details.getStudentId() == null) {
            throw new RuntimeException("Student ID required");
        }

        if (details.getMemberType().equalsIgnoreCase("Staff") && details.getStaffId() == null) {
            throw new RuntimeException("Staff ID required");
        }

        return libraryMemberDao.addMember(details, schoolCode);
    }

    @Override
    public List<LibraryMemberDetails> getAllMembers(String schoolCode) throws Exception {
        return libraryMemberDao.getAllMembers(schoolCode);
    }
}