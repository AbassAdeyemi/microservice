package com.hayba.microservices.kafka.to.elastic.service.consumer.impl;

import com.hayba.microservices.config.KafkaConfigData;
import com.hayba.microservices.elastic.index.client.service.ElasticIndexClient;
import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.kafka.admin.client.KafkaAdminClient;
import com.hayba.microservices.kafka.avro.model.TwitterAvroModel;
import com.hayba.microservices.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.hayba.microservices.kafka.to.elastic.service.transformer.AvroToElasticModelTransformer;
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
public class TwitterKafkaConsumer implements KafkaConsumer<Long, TwitterAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfigData;

    private final AvroToElasticModelTransformer avroToElasticModelTransformer;

    private final ElasticIndexClient elasticIndexClient;

    public TwitterKafkaConsumer(KafkaAdminClient kafkaAdminClient, KafkaConfigData kafkaConfigData,
                                KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
                                AvroToElasticModelTransformer avroToElasticModelTransformer,
                                ElasticIndexClient elasticIndexClient) {
        this.kafkaAdminClient = kafkaAdminClient;
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
        this.avroToElasticModelTransformer = avroToElasticModelTransformer;
        this.elasticIndexClient = elasticIndexClient;
    }

    @EventListener
    public void onAppStartup(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topic with name {} is ready for operation", kafkaConfigData.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("twitterTopicListener").start();
    }

    @Override
    @KafkaListener(id = "twitterTopicListener", topics = "${kafka-config.topic-name}")
    public void receive(@Payload List<TwitterAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        LOG.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to elastic: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

        List<TwitterIndexModel> twitterIndexModels = avroToElasticModelTransformer.getElasticModels(messages);
        List<String> documentIds = elasticIndexClient.save(twitterIndexModels);
        LOG.info("Documents saved to elasticsearch with ids {}", documentIds.toArray());
    }


}
