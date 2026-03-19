package com.sms.dao;

import com.sms.model.RefreshToken;

import java.time.Instant;

public interface RefreshTokenDao {
    public RefreshToken findByUserName(String userName);
    public RefreshToken findByRefreshToken(String refreshToken);
    public void save(RefreshToken refreshToken);
    public void updateExpiryTime(String userName, Instant newExpiryTime);
    public void deleteByRefreshToken(String refreshToken);
}
