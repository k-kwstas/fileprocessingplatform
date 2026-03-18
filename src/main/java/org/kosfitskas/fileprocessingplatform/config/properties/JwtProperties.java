package org.kosfitskas.fileprocessingplatform.config.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.jwt")
@Validated
@RequiredArgsConstructor
@Data
public class JwtProperties {

    @NotBlank
    private String secret;

    @Min(1000)
    private long expirationMs;

    @NotBlank
    private String issuer;
}
