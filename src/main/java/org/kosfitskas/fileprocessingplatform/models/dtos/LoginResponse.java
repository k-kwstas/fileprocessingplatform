package org.kosfitskas.fileprocessingplatform.models.dtos;

import org.kosfitskas.fileprocessingplatform.models.RoleEntity;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(String accessToken, String tokenType, UUID userId, String username, String email, Set<RoleEntity> roles) {
}
