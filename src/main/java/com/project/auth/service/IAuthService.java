package com.project.auth.service;

import com.project.auth.dto.*;

import java.util.List;

public interface IAuthService {

    // Authentication & Registration
    String saveUser(CreateUserDto createUserDto);
    TokenResponse generateToken(AuthRequest authRequest);
    String validateToken(String token);
    UserInfoResponse extractUserInfo(String token);
    TokenResponse refreshToken(RefreshTokenRequest request);
    void logout(String refreshToken);
    
    // User Profile Management
    UserProfileResponse getUserProfile(String userId);
    UserProfileResponse updateUserProfile(String userId, CreateUserDto updateDto);
    void changePassword(String userId, ChangePasswordRequest request);
    void forgotPassword(ForgotPasswordRequest request);
    
    // Admin Operations
    List<UserProfileResponse> getAllUsers();
    List<UserProfileResponse> getUsersByRole(String role);
    void activateUser(String userId);
    void deactivateUser(String userId);
    void deleteUser(String userId);
}
