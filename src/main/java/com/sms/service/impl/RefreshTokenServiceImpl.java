package com.sms.service.impl;

import com.sms.dao.RefreshTokenDao;
import com.sms.model.RefreshToken;
import com.sms.model.RegistrationDetails;
import com.sms.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private static final long refreshTokenValidity = 1000 * 60 * 2;
    @Autowired
    private RefreshTokenDao refreshTokenDao;
    @Override
    public RefreshToken createRefreshToken(String userName) {
        RefreshToken refreshToken = refreshTokenDao.findByUserName(userName);

        if (refreshToken == null) {
            // Token does not exist, so we create a new one
            refreshToken = new RefreshToken();
            refreshToken.setUserName(userName);
            refreshToken.setRefreshToken(UUID.randomUUID().toString());
            refreshToken.setExpiryTime(Instant.now().plusMillis(refreshTokenValidity));
            refreshTokenDao.save(refreshToken);
        } else {
            // Token exists, just update the expiry time
            refreshToken.setExpiryTime(Instant.now().plusMillis(refreshTokenValidity));
            refreshTokenDao.updateExpiryTime(refreshToken.getUserName(), refreshToken.getExpiryTime());
        }

        return refreshToken;
    }

    @Override
    public RefreshToken verifyRefreshToken(String refreshToken) {
        RefreshToken tokenFromDb = refreshTokenDao.findByRefreshToken(refreshToken);

        if (tokenFromDb == null) {
            throw new RuntimeException("Token is not available in database");
        }

        if (tokenFromDb.getExpiryTime().compareTo(Instant.now()) < 0) {
            // Token has expired, delete it and throw an exception
            refreshTokenDao.deleteByRefreshToken(refreshToken);
            throw new RuntimeException("Refresh Token Expired!");
        }

        return tokenFromDb;
    }
}
