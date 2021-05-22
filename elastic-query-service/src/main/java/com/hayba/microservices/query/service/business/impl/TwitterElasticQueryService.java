package com.hayba.microservices.query.service.business.impl;

import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.elastic.query.client.service.ElasticQueryClient;
import com.hayba.microservices.query.service.business.ElasticQueryService;
import com.hayba.microservices.query.service.model.ElasticQueryServiceResponseModel;
import com.hayba.microservices.query.service.transformer.ElasticToResponseModelTransformer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class TwitterElasticQueryService implements ElasticQueryService {
    private final ElasticToResponseModelTransformer responseModelTransformer;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.info("Querying elasticsearch by id: {}", id);
        return responseModelTransformer.getResponseModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        log.info("Querying elasticsearch by text: {}", text);
        return responseModelTransformer.getResponseModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.info("Querying elasticsearch for all documents");
        return responseModelTransformer.getResponseModels(elasticQueryClient.getAllIndexModels());
    }
}
