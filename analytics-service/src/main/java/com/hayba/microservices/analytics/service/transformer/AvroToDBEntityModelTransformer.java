package com.hayba.microservices.analytics.service.transformer;

import com.hayba.microservices.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.hayba.microservices.kafka.avro.model.TwitterAnalyticsAvroModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.IdGenerator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AvroToDBEntityModelTransformer {
    private final IdGenerator idGenerator;

    public List<AnalyticsEntity> getEntityModel(List<TwitterAnalyticsAvroModel> avroModels) {
        return avroModels.stream()
                .map(avroModel -> new AnalyticsEntity(
                        idGenerator.generateId(),
                        avroModel.getWord(),
                        avroModel.getWordCount(),
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(avroModel.getCreatedAt()), ZoneOffset.UTC)
                )).collect(Collectors.toList());
    }

}
