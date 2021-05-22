package com.hayba.microservices.elastic.query.client.service.impl;

import com.hayba.microservices.config.ElasticConfigData;
import com.hayba.microservices.config.ElasticQueryConfigData;
import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import com.hayba.microservices.elastic.query.client.exception.ElasticQueryClientException;
import com.hayba.microservices.elastic.query.client.service.ElasticQueryClient;
import com.hayba.microservices.elastic.query.client.util.ElasticQueryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final ElasticConfigData elasticConfigData;

    private final ElasticQueryConfigData elasticQueryConfigData;

    private final ElasticsearchOperations elasticsearchOperations;

    private final ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil;

    public TwitterElasticQueryClient(ElasticConfigData elasticConfigData,
                                     ElasticQueryConfigData elasticQueryConfigData,
                                     ElasticsearchOperations elasticsearchOperations,
                                     ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil) {
        this.elasticConfigData = elasticConfigData;
        this.elasticQueryConfigData = elasticQueryConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticQueryUtil = elasticQueryUtil;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> searchResult = elasticsearchOperations.searchOne(
                query, TwitterIndexModel.class, IndexCoordinates.of(elasticConfigData.getIndexName())
        );

        if (searchResult == null) {
            LOG.error("No document found with id: {]", id);
            throw new ElasticQueryClientException("No document found with id: " + id);
        }

        LOG.info("Document with id: {} retrieved successfully", searchResult.getId());
        return searchResult.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        SearchHits<TwitterIndexModel> searchResults = elasticsearchOperations.search(
                query, TwitterIndexModel.class, IndexCoordinates.of(elasticConfigData.getIndexName())
        );
        LOG.info("{} number of documents with text {} retrieved successfully" + searchResults.getTotalHits(), text);
        return searchResults.stream().map(s -> s.getContent()).collect(Collectors.toList());
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        SearchHits<TwitterIndexModel> searchResults = elasticsearchOperations.search(
                query, TwitterIndexModel.class, IndexCoordinates.of(elasticConfigData.getIndexName()));

        LOG.info("{} number of documents with retrieved successfully" + searchResults.getTotalHits());
        return searchResults.stream().map(s -> s.getContent()).collect(Collectors.toList());
    }
}
