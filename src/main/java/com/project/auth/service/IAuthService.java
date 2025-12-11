package com.project.auth.service;

import com.project.auth.dto.AuthRequest;
import com.project.auth.dto.CreateUserDto;

public interface IAuthService {

    String saveUser(CreateUserDto createUserDto);
    String generateToken(AuthRequest authRequest);
    String validateToken(String token);
}
