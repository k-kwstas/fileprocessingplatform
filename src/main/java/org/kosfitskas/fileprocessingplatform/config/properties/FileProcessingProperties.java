package org.kosfitskas.fileprocessingplatform.config.properties;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties(prefix = "app.fileprocessing")
@Validated
@RequiredArgsConstructor
@Data
public class FileProcessingProperties {
    @Min(1)
    private int maxFileSizeMb;
    private List<String> allowedExtensions = new ArrayList<>();
}