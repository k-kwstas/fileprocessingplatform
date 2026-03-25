package org.kos.fileprocessingplatform.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kos.fileprocessingplatform.config.properties.Folders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqHandler {
    private final Folders properties;
    private final JmsTemplate jmsTemplate;
    @Value("${app.mq.queue-out}")
    private String queueOut;
    private static final String FILE_NAME = "fileName";
    private static final String JSON = ".json";
    private final XmlMapper xmlMapper = new XmlMapper();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @JmsListener(destination = "${app.mq.queue-in}")
    public void receiveFile(Message<String> message) {
        String xmlContent = message.getPayload();
        Object fileNameHeader = message.getHeaders().get(FILE_NAME);
        String fileName = fileNameHeader != null ? fileNameHeader.toString() : "unknown.xml";

        try {
            log.info("Received XML file: {}", fileName);

            String jsonContent = convertXmlToJson(xmlContent);

            String jsonFileName = toJsonFileName(fileName);

            jmsTemplate.convertAndSend(queueOut, jsonContent, jmsMessage -> {
                jmsMessage.setStringProperty(FILE_NAME, jsonFileName);
                jmsMessage.setStringProperty("originalFileName", fileName);
                jmsMessage.setStringProperty("contentType", "application/json");
                return jmsMessage;
            });

            log.info("Converted {} to JSON and sent to {}", fileName, queueOut);

        } catch (Exception e) {
            log.error("Failed to convert file {} from XML to JSON", fileName, e);
            throw new IllegalArgumentException("Invalid XML content", e);
        }
    }

    private String convertXmlToJson(String xmlContent) throws IOException {
        JsonNode jsonNode = xmlMapper.readTree(xmlContent.getBytes());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }


    @JmsListener(destination = "${app.mq.queue-out}")
    public void receiveFileWithHeaders(Message<String> message) {
        String jsonContent = message.getPayload();
        Object fileNameHeader = message.getHeaders().get(FILE_NAME);
        Object originalFileNameHeader = message.getHeaders().get("originalFileName");

        String fileName = fileNameHeader != null ? fileNameHeader.toString() : "output.json";
        String originalFileName = originalFileNameHeader != null ? originalFileNameHeader.toString() : null;

        try {
            writeJsonToOutputFolder(fileName, jsonContent);
            if (originalFileName != null && !originalFileName.isBlank()) {
                moveOriginalFileToArchive(originalFileName);
            }

            log.info("Created file {} in output folder", fileName);
        } catch (Exception e) {
            log.error("Failed to write file {} to output folder", fileName, e);
        }
    }

    private void writeJsonToOutputFolder(String fileName, String jsonContent) throws IOException {
        String safeFileName = toJsonFileName(fileName);

        Path outputDir = Path.of(properties.getOutput());
        Files.createDirectories(outputDir);

        Path outputFile = outputDir.resolve(safeFileName);
        Files.writeString(outputFile, jsonContent, StandardCharsets.UTF_8);

        log.info("File written to {}", outputFile.toAbsolutePath());
    }

    private String toJsonFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return originalFileName + JSON;
        }

        int lastDot = originalFileName.lastIndexOf('.');
        if (lastDot == -1) {
            return originalFileName + JSON;
        }

        return originalFileName.substring(0, lastDot) + JSON;
    }

    private void moveOriginalFileToArchive(String originalFileName) throws IOException {
        Path inputDir = Path.of(properties.getInput());
        Path archiveDir = Path.of(properties.getArchive());

        Files.createDirectories(archiveDir);

        Path sourceFile = inputDir.resolve(originalFileName);
        Path targetFile = archiveDir.resolve(originalFileName);

        if (!Files.exists(sourceFile)) {
            log.warn("Original file not found for archive: {}", sourceFile.toAbsolutePath());
            return;
        }

        Files.move(sourceFile, targetFile, StandardCopyOption.REPLACE_EXISTING);

        log.info("Moved original file to archive: {}", targetFile.toAbsolutePath());
    }
}