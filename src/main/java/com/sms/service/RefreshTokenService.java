package com.sms.service;

import com.sms.model.RefreshToken;

public interface RefreshTokenService {
    public RefreshToken createRefreshToken(String userName);
    public RefreshToken verifyRefreshToken(String refreshToken);
}
