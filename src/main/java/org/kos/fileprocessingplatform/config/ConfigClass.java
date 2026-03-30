package org.kos.fileprocessingplatform.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ibm.mq.jakarta.jms.MQConnectionFactory;

import org.apache.camel.component.jms.JmsComponent;
import com.ibm.msg.client.jakarta.wmq.WMQConstants;
import jakarta.jms.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.kos.fileprocessingplatform.config.properties.MqProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.task.support.TaskExecutorAdapter;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import tools.jackson.databind.ObjectMapper;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Configuration
public class ConfigClass {


    private final MqProperties properties;

    @Bean
    public XmlMapper xmlMapper() {
        return new XmlMapper();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Executor taskExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }

    @Bean
    public ConnectionFactory mqConnectionFactory(
            ResourceLoader resourceLoader
    ) throws Exception {

        String[] hostPort = parseConnName(properties.getConnName());

        System.out.println("connName=" + properties.getConnName());
        System.out.println("sslCipherSpec=" + properties.getSslCipherSpec());
        System.out.println("sslTrustStore=" + properties.getSslTrustStore());
        System.out.println("sslTrustStorePassword=" + properties.getSslTrustStorePassword());

        MQConnectionFactory cf = new MQConnectionFactory();
        cf.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        cf.setQueueManager(properties.getQueueManager());
        cf.setChannel(properties.getChannel());
        cf.setHostName(hostPort[0]);
        cf.setPort(Integer.parseInt(hostPort[1]));

        // Credentials
        cf.setStringProperty(WMQConstants.USERID, properties.getUser());
        cf.setStringProperty(WMQConstants.PASSWORD, properties.getPassword());
        // TLS: required so the custom socket factory is actually used
        cf.setStringProperty(WMQConstants.WMQ_SSL_CIPHER_SPEC, properties.getSslCipherSpec());

        Resource resource = resourceLoader.getResource(properties.getSslTrustStore());

        System.out.println("truststore exists=" + resource.exists());
        System.out.println("truststore description=" + resource.getDescription());

        KeyStore trustStore = KeyStore.getInstance("JKS");
        try (InputStream is = resource.getInputStream()) {
            trustStore.load(is, properties.getSslTrustStorePassword().toCharArray());
        }

        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        cf.setSSLSocketFactory(sslContext.getSocketFactory());

        return cf;
    }

//    @Bean
//    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
//            ConnectionFactory connectionFactory) {
//
//        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
//        factory.setConnectionFactory(connectionFactory);
//        factory.setTaskExecutor(new TaskExecutorAdapter(Executors.newVirtualThreadPerTaskExecutor()));
//        return factory;
//    }
    @Bean
    public JmsComponent jmsComponent(MQConnectionFactory mqConnectionFactory
    ) {
        JmsComponent jmsComponent = new JmsComponent();
        jmsComponent.setConnectionFactory(mqConnectionFactory);
        return jmsComponent;
    }


    private String[] parseConnName(String connName) {
        int open = connName.lastIndexOf('(');
        int close = connName.lastIndexOf(')');
        if (open < 1 || close < open + 1) {
            throw new IllegalArgumentException("Invalid ibm.mq.connName format. Expected host(port), got: " + connName);
        }

        String host = connName.substring(0, open).trim();
        String port = connName.substring(open + 1, close).trim();
        return new String[]{host, port};
    }
}
