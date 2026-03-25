package org.kosfitskas.fileprocessingplatform.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.kosfitskas.fileprocessingplatform.models.dtos.LoginRequest;
import org.kosfitskas.fileprocessingplatform.models.dtos.LoginResponse;
import org.kosfitskas.fileprocessingplatform.models.dtos.RegisterRequest;
import org.kosfitskas.fileprocessingplatform.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/api")
@SecurityRequirements
public class AuthController {

    private final AuthService service;

    @PostMapping("/v1/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerUser(request));
    }

    @PostMapping("/v1/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }
}
