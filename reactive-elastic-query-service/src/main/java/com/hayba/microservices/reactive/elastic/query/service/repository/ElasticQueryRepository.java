package com.hayba.microservices.reactive.elastic.query.service.repository;

import com.hayba.microservices.elastic.model.index.impl.TwitterIndexModel;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ElasticQueryRepository extends ReactiveCrudRepository<TwitterIndexModel, String> {

    Flux<TwitterIndexModel> findByText(String text);
}
