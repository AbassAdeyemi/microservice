package com.hayba.microservices.analytics.service.business.impl;

import com.hayba.microservices.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.hayba.microservices.analytics.service.dataaccess.repository.AnalyticsRepository;
import com.hayba.microservices.analytics.service.transformer.AvroToDBEntityModelTransformer;
import com.hayba.microservices.config.KafkaConfigData;
import com.hayba.microservices.kafka.admin.client.KafkaAdminClient;
import com.hayba.microservices.kafka.avro.model.TwitterAnalyticsAvroModel;
import com.hayba.microservices.kafka.consumer.api.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsKafkaConsumer implements KafkaConsumer<TwitterAnalyticsAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsKafkaConsumer.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfig;

    private final AvroToDBEntityModelTransformer avroToDBEntityModelTransformer;

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsKafkaConsumer(KafkaListenerEndpointRegistry registry,
                                  KafkaAdminClient adminClient,
                                  KafkaConfigData config, AvroToDBEntityModelTransformer avroToDBEntityModelTransformer,
                                  AnalyticsRepository analyticsRepository) {
        this.kafkaListenerEndpointRegistry = registry;
        this.kafkaAdminClient = adminClient;
        this.kafkaConfig = config;
        this.avroToDBEntityModelTransformer = avroToDBEntityModelTransformer;
        this.analyticsRepository = analyticsRepository;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfig.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("twitterAnalyticsTopicListener").start();
    }

    @Override
    @KafkaListener(id = "twitterAnalyticsTopicListener", topics = "${kafka-config.topic-name}", autoStartup = "false")
    public void receive(@Payload List<TwitterAnalyticsAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Long> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        LOG.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to database: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

        List<AnalyticsEntity> twitterAnalyticsEntities = avroToDBEntityModelTransformer.getEntityModel(messages);
        analyticsRepository.batchPersist(twitterAnalyticsEntities);
        LOG.info("{} number of messages sent to database", twitterAnalyticsEntities.size());
    }

}
