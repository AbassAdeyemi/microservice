package com.hayba.microservices.query.service.business.impl;

import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.elastic.query.client.service.ElasticQueryClient;
import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.hayba.microservices.query.service.assembler.ElasticQueryServiceResponseModelAssembler;
import com.hayba.microservices.query.service.business.ElasticQueryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class TwitterElasticQueryService implements ElasticQueryService {
    private final ElasticQueryServiceResponseModelAssembler assembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.info("Querying elasticsearch by id: {}", id);
        return assembler.toModel(elasticQueryClient.getIndexModelById(id));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        log.info("Querying elasticsearch by text: {}", text);
        return assembler.toModels(elasticQueryClient.getIndexModelByText(text));
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.info("Querying elasticsearch for all documents");
        return assembler.toModels(elasticQueryClient.getAllIndexModels());
    }
}
