package com.project.auth.controller;

import com.project.auth.dto.AuthRequest;
import com.project.auth.dto.CreateUserDto;
import com.project.auth.service.IAuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/register")
    public ResponseEntity<String> save(@RequestBody CreateUserDto createUserDto){
        return new ResponseEntity<>(iAuthService.saveUser(createUserDto),HttpStatus.CREATED);
    }

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody AuthRequest authRequest) {
      return new ResponseEntity<>(iAuthService.generateToken(authRequest),HttpStatus.OK);
    }

    @GetMapping("/validate")
    public ResponseEntity<String> validateToken(@RequestParam("token") String token) {
      return new ResponseEntity<>(iAuthService.validateToken(token),HttpStatus.OK);
    }
}
