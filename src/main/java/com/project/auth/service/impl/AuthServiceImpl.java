package com.project.auth.service.impl;

import com.project.auth.dto.*;
import com.project.auth.entity.RefreshToken;
import com.project.auth.entity.User;
import com.project.auth.exception.UserNotFoundException;
import com.project.auth.mapper.AuthApplicationEntityMapper;
import com.project.auth.repository.UserRepository;
import com.project.auth.security.JwtService;
import com.project.auth.service.IAuthService;
import com.project.auth.service.IRefreshTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AuthServiceImpl implements IAuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthApplicationEntityMapper authApplicationEntityMapper;
    private final AuthenticationManager authenticationManager;
    private final IRefreshTokenService refreshTokenService;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           AuthApplicationEntityMapper authApplicationEntityMapper,
                           IRefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.authApplicationEntityMapper = authApplicationEntityMapper;
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    public String saveUser(CreateUserDto createUserDto) {
        User user = authApplicationEntityMapper.createUserEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user.getId().toString();
    }

    @Override
    public TokenResponse generateToken(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        if (authenticate.isAuthenticated()) {
            User user = userRepository.findByEmail(authRequest.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Generate access token
            String accessToken = jwtService.generateToken(
                    user.getEmail(),
                    user.getId().toString(),
                    user.getRole()
            );

            // Generate refresh token
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken.getToken())
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000) // Convert to seconds
                    .build();
        } else {
            throw new UserNotFoundException("Invalid access");
        }
    }

    @Override
    public String validateToken(String token) {
        jwtService.validateToken(token);
        return "Token is valid";
    }

    @Override
    public UserInfoResponse extractUserInfo(String token) {
        try {
            jwtService.validateToken(token);
            String email = jwtService.extractUsername(token);
            String userId = jwtService.extractUserId(token);
            String role = jwtService.extractRole(token);

            return UserInfoResponse.builder()
                    .userId(userId)
                    .email(email)
                    .role(role)
                    .isValid(true)
                    .build();
        } catch (Exception e) {
            return UserInfoResponse.builder()
                    .isValid(false)
                    .build();
        }
    }

    @Override
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    // Generate new access token
                    String accessToken = jwtService.generateToken(
                            user.getEmail(),
                            user.getId().toString(),
                            user.getRole()
                    );

                    // Optionally: Generate new refresh token (rotation strategy)
                    // RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

                    return TokenResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestRefreshToken) // Return same refresh token
                            // .refreshToken(newRefreshToken.getToken()) // Or return new refresh token
                            .tokenType("Bearer")
                            .expiresIn(jwtService.getAccessTokenExpirationMs() / 1000)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.findByToken(refreshToken)
                .ifPresent(token ->
                        refreshTokenService.deleteByUser(token.getUser())
                );
    }

    @Override
    public UserProfileResponse getUserProfile(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return UserProfileResponse.builder()
                .id(user.getId().toString())
                .firstname(user.getFirstname())
                .lastname(user.getLastname())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .isVerified(user.getIsVerified())
                .profileImageUrl(user.getProfileImageUrl())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public UserProfileResponse updateUserProfile(String userId, CreateUserDto updateDto) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update fields (don't update password or role here)
        user.setFirstname(updateDto.getFirstname());
        user.setLastname(updateDto.getLastname());
        user.setPhone(updateDto.getPhone());

        user = userRepository.save(user);

        return getUserProfile(user.getId().toString());
    }

    @Override
    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }

        // Update to new password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Revoke all refresh tokens to force re-login
        refreshTokenService.revokeAllUserTokens(user);
    }

    @Override
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // TODO: Generate password reset token and send email
        // For now, just log it (you can implement email service later)
        String resetToken = java.util.UUID.randomUUID().toString();
        log.info("Password reset token for {}: {}", user.getEmail(), resetToken);

        // In real implementation:
        // 1. Generate reset token and save to database with expiry
        // 2. Send email with reset link
        // 3. Create endpoint to verify token and reset password
    }

    @Override
    public List<UserProfileResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> getUserProfile(user.getId().toString()))
                .toList();
    }

    @Override
    public List<UserProfileResponse> getUsersByRole(String role) {
        return userRepository.findAll().stream()
                .filter(user -> user.getRole().equalsIgnoreCase(role))
                .map(user -> getUserProfile(user.getId().toString()))
                .toList();
    }

    @Override
    public void activateUser(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setIsActive(true);
        userRepository.save(user);
    }

    @Override
    public void deactivateUser(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.setIsActive(false);
        userRepository.save(user);

        // Revoke all refresh tokens
        refreshTokenService.revokeAllUserTokens(user);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(java.util.UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Delete all refresh tokens first
        refreshTokenService.deleteByUser(user);

        // Delete user
        userRepository.delete(user);
    }
}