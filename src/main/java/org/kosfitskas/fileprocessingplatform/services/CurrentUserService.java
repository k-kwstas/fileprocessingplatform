package org.kosfitskas.fileprocessingplatform.services;

import org.kosfitskas.fileprocessingplatform.models.UserEntity;

public interface CurrentUserService {
    UserEntity getCurrentUser();

    boolean isAdmin(UserEntity user);
}
