package org.kos.fileprocessingplatform.services;

import org.kos.fileprocessingplatform.models.FileJobEntity;

import java.util.List;
import java.util.UUID;

public interface FileJobService {
    FileJobEntity getJobById(UUID jobId);

    List<FileJobEntity> getCurrentUserJobs();

    void deleteJob(UUID jobId);
}
