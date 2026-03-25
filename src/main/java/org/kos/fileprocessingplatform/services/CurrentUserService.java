package org.kos.fileprocessingplatform.services;

import org.kos.fileprocessingplatform.models.UserEntity;

public interface CurrentUserService {
    UserEntity getCurrentUser();

    boolean isAdmin(UserEntity user);
}
