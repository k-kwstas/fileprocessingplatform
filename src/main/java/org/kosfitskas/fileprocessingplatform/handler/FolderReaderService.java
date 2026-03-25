package org.kosfitskas.fileprocessingplatform.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosfitskas.fileprocessingplatform.config.properties.Folders;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
@Component
@RequiredArgsConstructor
public class FolderReaderService {

    private final Folders folders;
    private final MqService mqService;

    @Scheduled(fixedDelay = 1000)
    public void pollInputFolder() {
        Path inputDir = Path.of(folders.getInput());

        try {
            Files.createDirectories(inputDir);

            try (Stream<Path> files = Files.list(inputDir)) {
                files.filter(Files::isRegularFile)
                        .filter(this::isXmlFile)
                        .forEach(this::processSafely);
            }

        } catch (Exception e) {
            log.error("Failed to poll input folder {}", inputDir.toAbsolutePath(), e);
        }
    }

    private boolean isXmlFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".xml");
    }

    private void processSafely(Path filePath) {
        try {
            log.info("Found file in input folder: {}", filePath.toAbsolutePath());
            mqService.initJobFile(filePath);
        } catch (Exception e) {
            log.error("Failed to process file {}", filePath.toAbsolutePath(), e);
        }
    }
}