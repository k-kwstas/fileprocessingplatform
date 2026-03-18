package org.kosfitskas.fileprocessingplatform.services;

import org.kosfitskas.fileprocessingplatform.models.dtos.LoginRequest;
import org.kosfitskas.fileprocessingplatform.models.dtos.LoginResponse;
import org.kosfitskas.fileprocessingplatform.models.dtos.RegisterRequest;

public interface AuthService {
    String registerUser(RegisterRequest request);


    LoginResponse login(LoginRequest request);
}
