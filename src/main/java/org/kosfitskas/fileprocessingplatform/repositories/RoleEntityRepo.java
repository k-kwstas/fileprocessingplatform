package org.kosfitskas.fileprocessingplatform.repositories;

import org.kosfitskas.fileprocessingplatform.models.ERole;
import org.kosfitskas.fileprocessingplatform.models.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleEntityRepo extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByName(ERole name);
}

