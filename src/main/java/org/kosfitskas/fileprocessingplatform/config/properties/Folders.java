package org.kosfitskas.fileprocessingplatform.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.folders")
public class Folders {

    private String input;
    private String output;
    private String archive;

}
