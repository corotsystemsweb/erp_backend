package com.sms.service.impl;

import com.sms.dao.SessionDao;
import com.sms.model.SessionDetails;
import com.sms.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class SessionServiceImpl implements SessionService {
    @Autowired
    private SessionDao sessionDao;
    @Override
    public SessionDetails addSession(SessionDetails sessionDetails, String schoolCode) throws Exception {
        return sessionDao.addSession(sessionDetails, schoolCode);
    }

    @Override
    public SessionDetails getSessionById(int sessionId, String schoolCode) throws Exception {
        return sessionDao.getSessionById(sessionId, schoolCode);
    }

    @Override
    public List<SessionDetails> getAllSessionDetails(String schoolCode) throws Exception {
        return sessionDao.getAllSessionDetails(schoolCode);
    }

    @Override
    public SessionDetails updateSessionDetailsById(SessionDetails sessionDetails, int sessionId, String schoolCode) throws Exception {
        return sessionDao.updateSessionDetailsById(sessionDetails, sessionId, schoolCode);
    }

    @Override
    public boolean deleteSessionDetailsById(int sessionId, String schoolCode) throws Exception {
        return sessionDao.deleteSessionDetailsById(sessionId, schoolCode);
    }
}
