package com.project.auth.controller;

import com.project.auth.dto.*;
import com.project.auth.service.IAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IAuthService iAuthService;
    private final AuthenticationManager authenticationManager;

    public AuthController(IAuthService iAuthService,
                          AuthenticationManager authenticationManager){
        this.iAuthService=iAuthService;
        this.authenticationManager=authenticationManager;
    }

    // ===================== Authentication Endpoints =====================
    
    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody CreateUserDto createUserDto){
        return new ResponseEntity<>(iAuthService.saveUser(createUserDto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        return new ResponseEntity<>(iAuthService.generateToken(authRequest), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return new ResponseEntity<>(iAuthService.refreshToken(request), HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest request) {
        iAuthService.logout(request.getRefreshToken());
        return new ResponseEntity<>("Logged out successfully", HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
        return new ResponseEntity<>(iAuthService.validateToken(token), HttpStatus.OK);
    }

    @GetMapping("/user-info")
    public ResponseEntity<UserInfoResponse> getUserInfo(@RequestParam("token") String token) {
        return new ResponseEntity<>(iAuthService.extractUserInfo(token), HttpStatus.OK);
    }

    // ===================== User Profile Endpoints =====================
    
    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String userId) {
        return new ResponseEntity<>(iAuthService.getUserProfile(userId), HttpStatus.OK);
    }

    @PutMapping("/profile/{userId}")
    public ResponseEntity<UserProfileResponse> updateProfile(
            @PathVariable String userId,
            @Valid @RequestBody CreateUserDto updateDto) {
        return new ResponseEntity<>(iAuthService.updateUserProfile(userId, updateDto), HttpStatus.OK);
    }

    @PostMapping("/change-password/{userId}")
    public ResponseEntity<String> changePassword(
            @PathVariable String userId,
            @Valid @RequestBody ChangePasswordRequest request) {
        iAuthService.changePassword(userId, request);
        return new ResponseEntity<>("Password changed successfully", HttpStatus.OK);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        iAuthService.forgotPassword(request);
        return new ResponseEntity<>("Password reset instructions sent to email", HttpStatus.OK);
    }

    // ===================== Admin Endpoints =====================
    
    @GetMapping("/admin/users")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return new ResponseEntity<>(iAuthService.getAllUsers(), HttpStatus.OK);
    }

    @GetMapping("/admin/users/role/{role}")
    public ResponseEntity<List<UserProfileResponse>> getUsersByRole(@PathVariable String role) {
        return new ResponseEntity<>(iAuthService.getUsersByRole(role), HttpStatus.OK);
    }

    @PutMapping("/admin/users/{userId}/activate")
    public ResponseEntity<String> activateUser(@PathVariable String userId) {
        iAuthService.activateUser(userId);
        return new ResponseEntity<>("User activated successfully", HttpStatus.OK);
    }

    @PutMapping("/admin/users/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(@PathVariable String userId) {
        iAuthService.deactivateUser(userId);
        return new ResponseEntity<>("User deactivated successfully", HttpStatus.OK);
    }

    @DeleteMapping("/admin/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable String userId) {
        iAuthService.deleteUser(userId);
        return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
    }
}
