package org.kos.fileprocessingplatform.models.dtos;

import org.kos.fileprocessingplatform.models.RoleEntity;

import java.util.Set;
import java.util.UUID;

public record LoginResponse(String accessToken, String tokenType, UUID userId, String username, String email, Set<RoleEntity> roles) {
}
