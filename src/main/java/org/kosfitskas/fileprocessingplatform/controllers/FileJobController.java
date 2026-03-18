package org.kosfitskas.fileprocessingplatform.controllers;

import org.kosfitskas.fileprocessingplatform.models.FileJobEntity;
import org.kosfitskas.fileprocessingplatform.services.FileJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/jobs")
public class FileJobController {

    private final FileJobService fileJobService;

    public FileJobController(FileJobService fileJobService) {
        this.fileJobService = fileJobService;
    }

    @GetMapping
    public ResponseEntity<List<FileJobEntity>> getMyJobs() {
        return ResponseEntity.ok(fileJobService.getCurrentUserJobs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FileJobEntity> getJob(@PathVariable UUID id) {
        return ResponseEntity.ok(fileJobService.getJobById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable UUID id) {
        fileJobService.deleteJob(id);
        return ResponseEntity.noContent().build();
    }
}