package com.hayba.microservices.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-producer-config")
public class KafkaProducerConfigData {
    private String keySerializerClass;
    private String valueSerializerClass;
    private String compressionTYpe;
    private String acks;
    private Long batchSize;
    private Long batchSizeBoostFactor;
    private Integer lingerMs;
    private Integer requestTimeMs;
    private Integer retryCount;
}
