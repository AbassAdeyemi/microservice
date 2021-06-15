package com.hayba.microservices.reactive.elastic.query.service.business.impl;

import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.hayba.microservices.elastic.query.service.common.transformer.ElasticToResponseModelTransformer;
import com.hayba.microservices.reactive.elastic.query.service.business.ElasticQueryService;
import com.hayba.microservices.reactive.elastic.query.service.business.ReactiveElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryService.class);

    private final ReactiveElasticQueryClient<TwitterIndexModel> elasticQueryClient;
    private final ElasticToResponseModelTransformer responseModelTransformer;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> elasticQueryClient, ElasticToResponseModelTransformer responseModelTransformer) {
        this.elasticQueryClient = elasticQueryClient;
        this.responseModelTransformer = responseModelTransformer;
    }

    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        LOG.info("Querying reactive elasticsearch for {}", text);
        return elasticQueryClient
                .getIndexModelByText(text)
                .map(responseModelTransformer::getResponseModel);
    }
}
