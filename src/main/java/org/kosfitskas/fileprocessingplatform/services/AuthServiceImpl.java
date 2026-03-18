package org.kosfitskas.fileprocessingplatform.services;

import lombok.RequiredArgsConstructor;
import org.kosfitskas.fileprocessingplatform.components.jwtutils.JwtUtils;
import org.kosfitskas.fileprocessingplatform.models.ERole;
import org.kosfitskas.fileprocessingplatform.models.RoleEntity;
import org.kosfitskas.fileprocessingplatform.models.UserEntity;
import org.kosfitskas.fileprocessingplatform.models.dtos.LoginRequest;
import org.kosfitskas.fileprocessingplatform.models.dtos.LoginResponse;
import org.kosfitskas.fileprocessingplatform.models.dtos.RegisterRequest;
import org.kosfitskas.fileprocessingplatform.repositories.RoleEntityRepo;
import org.kosfitskas.fileprocessingplatform.repositories.UserEntityRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserEntityRepo userEntityRepo;
    private final RoleEntityRepo roleEntityRepo;
    private final PasswordEncoder encoder;
    private final JwtUtils  jwtUtils;
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Override
    public String registerUser(RegisterRequest request) {

        if (request.getUsername() != null && userEntityRepo.findByUsername(request.getUsername()).isPresent()) {
            logger.error("Username: {} is already in use", request.getUsername());
            throw new IllegalArgumentException("Username is already taken");
        }

        if (request.getEmail() != null && userEntityRepo.findByEmail(request.getEmail()).isPresent()) {
            logger.error("Username: {} is already in use", request.getEmail());
            throw new IllegalArgumentException("Email is already taken");
        }
        RoleEntity userRole = roleEntityRepo.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER not found"));

        var userEntity = UserEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(encoder.encode(request.getPassword()))
                .confirmPassword(encoder.encode(request.getConfirmPassword()))
                .creationDate(Instant.now())
                .roles(Set.of(userRole))
                .build();

        userEntityRepo.save(userEntity);
        logger.info("User {} has been registered", request.getUsername());
        return "Successfully registered user";
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        if (request.loginString() == null || request.password() == null) {
            logger.error("Invalid login or password");
            throw new RuntimeException("Username/email and password must not be null");
        }

        UserEntity user = userEntityRepo.findByUsername(request.loginString())
                .orElseGet(() -> userEntityRepo.findByEmail(request.loginString())
                        .orElseThrow(() -> {
                            logger.error("User with username/email: {} not found", request.loginString());
                            return new RuntimeException("User not found");
                        }));

        if (!encoder.matches(request.password(), user.getPassword())) {
            logger.error("Invalid password for user {}", request.loginString());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtils.generateJwtToken(user);

        return new LoginResponse(
                token,
                "Bearer",
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRoles()
        );
    }

}
