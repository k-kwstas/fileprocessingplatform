package org.kosfitskas.fileprocessingplatform.services;

import org.kosfitskas.fileprocessingplatform.models.FileJobEntity;

import java.util.List;
import java.util.UUID;

public interface FileJobService {
    FileJobEntity getJobById(UUID jobId);

    List<FileJobEntity> getCurrentUserJobs();

    void deleteJob(UUID jobId);
}
