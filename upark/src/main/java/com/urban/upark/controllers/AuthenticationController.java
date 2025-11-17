package com.urban.upark.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.urban.upark.configs.AuthenticationService;
import com.urban.upark.dto.auth.AuthenticationRequest;
import com.urban.upark.dto.auth.AuthenticationResponse;
import com.urban.upark.dto.auth.RegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
        @RequestBody RegisterRequest request ){
        return ResponseEntity.ok(service.register(request));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> login(
        @RequestBody AuthenticationRequest request){
        return ResponseEntity.ok(service.authenticate(request));
    }

    @PostMapping("/hash-password")
    public ResponseEntity<String> hashPassword(@RequestBody String password) {
        String hashedPassword = passwordEncoder.encode(password);
        return ResponseEntity.ok(hashedPassword);
    }

}
