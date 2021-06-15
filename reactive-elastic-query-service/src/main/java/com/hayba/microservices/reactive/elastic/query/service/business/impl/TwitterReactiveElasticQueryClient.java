package com.hayba.microservices.reactive.elastic.query.service.business.impl;

import com.hayba.microservices.config.ElasticQueryServiceConfigData;
import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import com.hayba.microservices.reactive.elastic.query.service.repository.ElasticQueryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
public class TwitterReactiveElasticQueryClient implements ReactiveElasticQueryClient<TwitterIndexModel> {

    private final ElasticQueryRepository elasticQueryRepository;
    private final ElasticQueryServiceConfigData configData;

    public TwitterReactiveElasticQueryClient(ElasticQueryRepository elasticQueryRepository, ElasticQueryServiceConfigData configData) {
        this.elasticQueryRepository = elasticQueryRepository;
        this.configData = configData;
    }

    @Override
    public Flux<TwitterIndexModel> getIndexModelByText(String text) {
        return elasticQueryRepository.findByText(text)
                .delayElements(Duration.ofMillis(configData.getBackPressureDelayMs()));
    }
}
