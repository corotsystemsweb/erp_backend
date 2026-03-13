package com.sms.service;

import com.sms.model.SessionDetails;

import java.util.List;

public interface SessionService {
    public SessionDetails addSession(SessionDetails sessionDetails, String schoolCode) throws Exception;
    public SessionDetails getSessionById(int sessionId, String schoolCode) throws Exception;
    public List<SessionDetails> getAllSessionDetails(String schoolCode) throws Exception;
    public SessionDetails updateSessionDetailsById(SessionDetails sessionDetails, int sessionId, String schoolCode) throws Exception;
    public boolean deleteSessionDetailsById(int sessionId, String schoolCode) throws Exception;
}
