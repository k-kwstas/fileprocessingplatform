package org.kosfitskas.fileprocessingplatform.services;

import lombok.RequiredArgsConstructor;
import org.kosfitskas.fileprocessingplatform.models.ERole;
import org.kosfitskas.fileprocessingplatform.models.UserEntity;
import org.kosfitskas.fileprocessingplatform.repositories.UserEntityRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService{

    private final UserEntityRepo userEntityRepo;

    @Override
    public UserEntity getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        return userEntityRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    @Override
    public boolean isAdmin(UserEntity user) {
        return user.getRoles()
                .stream()
                .anyMatch(role -> role.getName().name().equals(ERole.ROLE_ADMIN.name()));
    }
}
