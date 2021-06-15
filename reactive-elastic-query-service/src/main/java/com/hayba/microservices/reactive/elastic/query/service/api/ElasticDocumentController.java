package com.hayba.microservices.reactive.elastic.query.service.api;

import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.hayba.microservices.reactive.elastic.query.service.business.impl.TwitterElasticQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/documents")
public class ElasticDocumentController {
    private static final Logger LOG  = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final TwitterElasticQueryService elasticQueryService;

    public ElasticDocumentController(TwitterElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @GetMapping(value = "/get-doc-by-text", produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText( @Valid @RequestBody ElasticQueryServiceRequestModel
                                                                                requestModel) {
        Flux<ElasticQueryServiceResponseModel> response =  elasticQueryService
                .getDocumentByText(requestModel.getText());
        response = response.log();
        LOG.info("Returning from query reactive service for text, {}", requestModel.getText());
        return response;
    }
}
