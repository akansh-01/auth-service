package com.project.auth.service;

import com.project.auth.entity.RefreshToken;
import com.project.auth.entity.User;

import java.util.Optional;

public interface IRefreshTokenService {
    RefreshToken createRefreshToken(String userEmail);
    Optional<RefreshToken> findByToken(String token);
    RefreshToken verifyExpiration(RefreshToken token);
    void deleteByUser(User user);
    void revokeAllUserTokens(User user);
}
