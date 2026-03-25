package org.kos.fileprocessingplatform.services;

import org.kos.fileprocessingplatform.models.dtos.LoginRequest;
import org.kos.fileprocessingplatform.models.dtos.LoginResponse;
import org.kos.fileprocessingplatform.models.dtos.RegisterRequest;

public interface AuthService {
    String registerUser(RegisterRequest request);


    LoginResponse login(LoginRequest request);
}
