package com.urban.upark.configs;

import com.urban.upark.models.*;
import com.urban.upark.repositories.UsersRepository;
import com.urban.upark.dto.auth.RegisterRequest;
import com.urban.upark.dto.auth.AuthenticationRequest;
import com.urban.upark.dto.auth.AuthenticationResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UsersRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // @Bean
    public AuthenticationResponse register(RegisterRequest request) {
        var user= Users.builder()
                .name(request.getName())
                .first_name(request.getFirst_name())
                .userName(request.getUser_name())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone_number(request.getPhone_number())
                .role(Role.valueOf(request.getRole()))
                .build();
        repository.save(user);
        var jwtToken =jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // @Bean
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUser_name(),
                        request.getPassword()
                )
        );
        var user=repository.findByUserName(request.getUser_name())
                .orElseThrow();
        var jwtToken =jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

}
