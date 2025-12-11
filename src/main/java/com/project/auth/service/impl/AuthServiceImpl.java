package com.project.auth.service.impl;

import com.project.auth.dto.AuthRequest;
import com.project.auth.dto.CreateUserDto;
import com.project.auth.entity.User;
import com.project.auth.exception.UserNotFoundException;
import com.project.auth.mapper.AuthApplicationEntityMapper;
import com.project.auth.repository.UserRepository;
import com.project.auth.security.JwtService;
import com.project.auth.service.IAuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements IAuthService {


    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthApplicationEntityMapper authApplicationEntityMapper;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager,
                           AuthApplicationEntityMapper authApplicationEntityMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager=authenticationManager;
        this.authApplicationEntityMapper = authApplicationEntityMapper;
    }

    @Override
    public String saveUser(CreateUserDto createUserDto) {
        User user = authApplicationEntityMapper.createUserEntity(createUserDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user.getId().toString();
    }

    @Override
    public String generateToken(AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword())
        );
        if (authenticate.isAuthenticated()) {
            return jwtService.generateToken(authRequest.getEmail());
        } else {
            throw new UserNotFoundException("Invalid access");
        }
    }

    @Override
    public String validateToken(String token) {
        jwtService.validateToken(token);
        return "Token is valid";
    }
}