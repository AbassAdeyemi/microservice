package com.hayba.microservices.elastic.query.client.service.impl;

import com.hayba.microservices.common.util.CollectionsUtil;
import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.elastic.query.client.exception.ElasticQueryClientException;
import com.hayba.microservices.elastic.query.client.repository.TwitterElasticsearchQueryRepository;
import com.hayba.microservices.elastic.query.client.service.ElasticQueryClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {
    public static final Logger LOG = LoggerFactory.getLogger(TwitterElasticRepositoryQueryClient.class);

    private final TwitterElasticsearchQueryRepository elasticsearchRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticsearchQueryRepository elasticsearchRepository) {
        this.elasticsearchRepository = elasticsearchRepository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = elasticsearchRepository.findById(id);
        LOG.info("Document with id: {} retrieved successfully",
                searchResult.orElseThrow(() -> new ElasticQueryClientException("No document found with id: " + id))
                        .getId());

        return searchResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = elasticsearchRepository.findByText(text);
        LOG.info("{} number of documents with text {} retrieved successfully", searchResult.size(), text);
        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        List<TwitterIndexModel> searchResult = CollectionsUtil.getInstance().getListFromIterable(elasticsearchRepository.findAll());
        LOG.info("{} number of documents retrieved successfully", searchResult.size());
        return searchResult;
    }
}
