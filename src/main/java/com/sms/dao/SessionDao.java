package com.sms.dao;

import com.sms.model.SessionDetails;
import com.sms.model.StudentDetails;

import java.util.List;

public interface SessionDao {
    public SessionDetails addSession(SessionDetails sessionDetails, String schoolCode) throws Exception;
    public SessionDetails getSessionById(int sessionId, String schoolCode) throws Exception;
    public List<SessionDetails> getAllSessionDetails(String schoolCode) throws Exception;
    public SessionDetails updateSessionDetailsById(SessionDetails sessionDetails, int sessionId, String schoolCode) throws Exception;
    public boolean deleteSessionDetailsById(int sessionId, String schoolCode) throws Exception;
}
