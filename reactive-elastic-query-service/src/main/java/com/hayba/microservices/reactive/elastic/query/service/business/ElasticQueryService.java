package com.hayba.microservices.reactive.elastic.query.service.business;

import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryService {

    Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text);
}
