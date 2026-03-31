package org.kos.fileprocessingplatform.handler;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.kos.fileprocessingplatform.config.properties.Folders;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqHandler extends RouteBuilder {
    private static final String FILE_NAME = "fileName";
    private static final String ORIGINAL_FILE_NAME = "originalFileName";
    private static final String JSON = ".json";

    private final Folders properties;
    private final XmlMapper xmlMapper;
    private final ObjectMapper objectMapper;

    @Override
    public void configure() {
        from("jms:queue:{{app.mq.queue-in}}")
                .routeId("mq-xml-to-json")
                .process(this::transformXmlToJson)
                .to("jms:queue:{{app.mq.queue-out}}");

        from("jms:queue:{{app.mq.queue-out}}")
                .routeId("mq-json-to-output")
                .process(this::writeJsonOutput);
    }

    private void transformXmlToJson(Exchange exchange) {
        String xmlContent = exchange.getIn().getBody(String.class);
        String fileName = exchange.getIn().getHeader(FILE_NAME, "unknown.xml", String.class);

        try {
            log.info("Received XML file: {}", fileName);
            String jsonContent = convertXmlToJson(xmlContent);
            String jsonFileName = toJsonFileName(fileName);

            exchange.getMessage().setBody(jsonContent);
            exchange.getMessage().setHeader(FILE_NAME, jsonFileName);
            exchange.getMessage().setHeader(ORIGINAL_FILE_NAME, fileName);
            exchange.getMessage().setHeader("contentType", "application/json");
            log.info("Converted {} to JSON and sent to the output queue", fileName);
        } catch (Exception e) {
            log.error("Failed to convert file {} from XML to JSON", fileName, e);
            throw new IllegalArgumentException("Invalid XML content", e);
        }
    }

    private String convertXmlToJson(String xmlContent) throws IOException {
        JsonNode jsonNode = xmlMapper.readTree(xmlContent.getBytes());
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonNode);
    }


    private void writeJsonOutput(Exchange exchange) {
        String jsonContent = exchange.getIn().getBody(String.class);
        String fileName = exchange.getIn().getHeader(FILE_NAME, "output.json", String.class);
        String originalFileName = exchange.getIn().getHeader(ORIGINAL_FILE_NAME, String.class);

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
