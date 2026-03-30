package org.kos.fileprocessingplatform.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
@Data
@ConfigurationProperties(prefix = "ibm.mq")
public class MqProperties {

    private String queueManager;
    private String channel;
    private String connName;
    private String user;
    private String password;
    private String sslCipherSpec;
    private String sslTrustStore;
    private String sslTrustStorePassword;
}