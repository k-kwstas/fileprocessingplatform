package org.kosfitskas.fileprocessingplatform.repositories;

import org.kosfitskas.fileprocessingplatform.models.FileJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FileJobRepo extends JpaRepository<FileJobEntity, UUID> {
    Optional<FileJobEntity> findByIdAndOwnerId(UUID id, UUID ownerId);
    List<FileJobEntity> findAllByOwnerId(UUID ownerId);
}