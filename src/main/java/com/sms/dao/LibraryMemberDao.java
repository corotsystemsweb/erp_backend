package com.sms.dao;

import java.util.List;
import com.sms.model.LibraryMemberDetails;

public interface LibraryMemberDao {

    LibraryMemberDetails addMember(LibraryMemberDetails details, String schoolCode) throws Exception;

    List<LibraryMemberDetails> getAllMembers(String schoolCode) throws Exception;
}