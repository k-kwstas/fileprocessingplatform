package org.kosfitskas.fileprocessingplatform.controllers;

import lombok.RequiredArgsConstructor;
import org.kosfitskas.fileprocessingplatform.handler.MqService;
import org.kosfitskas.fileprocessingplatform.models.FileJobEntity;
import org.kosfitskas.fileprocessingplatform.services.FileJobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/jobs")
public class FileJobController {

    private final FileJobService fileJobService;
    private final MqService mqService;

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

    @PostMapping("/init-job-file")
    public ResponseEntity<FileJobEntity> initJobFile(@RequestPart("file")  MultipartFile file) throws IOException {
        mqService.initJobFile(file);
        return ResponseEntity.ok().build();
    }
}