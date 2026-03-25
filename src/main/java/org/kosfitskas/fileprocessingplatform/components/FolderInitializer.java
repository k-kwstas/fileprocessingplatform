package org.kosfitskas.fileprocessingplatform.components;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosfitskas.fileprocessingplatform.config.properties.FileProcessingProperties;
import org.kosfitskas.fileprocessingplatform.config.properties.Folders;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Component
@RequiredArgsConstructor
public class FolderInitializer {

    private final Folders properties;

    @PostConstruct
    public void init() {

        createFolder(properties.getInput(), "input");
        createFolder(properties.getOutput(), "output");
        createFolder(properties.getArchive(), "archive");
    }

    private void createFolder(String folderPath, String name) {
        if (folderPath == null || folderPath.isBlank()) {
            log.error("{} is null or blank", folderPath);
            throw new IllegalStateException("Missing " + name + " folder path");
        }

        try {
            Files.createDirectories(Path.of(folderPath));
            log.info("{} folder ready: {}", name, folderPath);
        } catch (IOException e) {
            log.error("{} could not be created", folderPath, e);
            throw new IllegalStateException("Could not create " + name + " folder: " + folderPath, e);
        }
    }
}
