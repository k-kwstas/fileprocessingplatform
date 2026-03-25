package org.kos.fileprocessingplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableJms
@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class FileProcessingPlatformApplication {

    static void main(String[] args) {
        SpringApplication.run(FileProcessingPlatformApplication.class, args);
    }

}
