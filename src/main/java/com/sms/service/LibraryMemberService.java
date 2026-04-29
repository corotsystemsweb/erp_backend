package com.sms.service;

import com.sms.model.LibraryMemberDetails;
import java.util.List;

public interface LibraryMemberService {

    LibraryMemberDetails addMember(LibraryMemberDetails details, String schoolCode) throws Exception;

    List<LibraryMemberDetails> getAllMembers(String schoolCode) throws Exception;
}
