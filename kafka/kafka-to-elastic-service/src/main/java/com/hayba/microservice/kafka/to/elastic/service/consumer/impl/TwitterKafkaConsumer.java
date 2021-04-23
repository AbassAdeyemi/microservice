package com.hayba.microservice.kafka.to.elastic.service.consumer.impl;

import com.hayba.microservice.kafka.to.elastic.service.consumer.KafkaConsumer;
import com.hayba.microservices.config.KafkaConfigData;
import com.hayba.microservices.kafka.admin.client.KafkaAdminClient;
import com.hayba.microservices.kafka.avro.model.TwitterAvroModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterKafkaConsumer implements KafkaConsumer<Long,TwitterAvroModel> {
    private  final Logger LOG = LoggerFactory.getLogger(TwitterKafkaConsumer.class);

    private final KafkaAdminClient adminClient;
    private final KafkaConfigData configData;

    public TwitterKafkaConsumer(KafkaAdminClient adminClient, KafkaConfigData configData) {
        this.adminClient = adminClient;
        this.configData = configData;
    }

    @Override
    @KafkaListener(id = "TwitterTopicListener")
    public void receive(@Payload List<TwitterAvroModel> messages, @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) List<Integer> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION_ID) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Integer> offsets) {

        LOG.info("Number of messages received with keys: {}, partitions: {} and offsets: {}, "+
                "sending it to elastic, Thread id: {}", messages.size(), keys.toString(), partitions.toString(),
                offsets.toString(), Thread.currentThread().getId());

    }
}
