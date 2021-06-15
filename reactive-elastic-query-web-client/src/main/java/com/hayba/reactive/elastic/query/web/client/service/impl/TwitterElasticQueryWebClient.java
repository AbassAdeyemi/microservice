package com.hayba.reactive.elastic.query.web.client.service.impl;

import com.hayba.microservices.config.ElasticQueryWebClientConfig;
import com.hayba.microservices.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.hayba.microservices.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.hayba.microservices.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.hayba.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {
    private final static Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);

    private final WebClient webClient;

    private final ElasticQueryWebClientConfig webClientConfig;

    public TwitterElasticQueryWebClient(@Qualifier("webClient") WebClient webClient, ElasticQueryWebClientConfig webClientConfig) {
        this.webClient = webClient;
        this.webClientConfig = webClientConfig;
    }

    @Override
    public Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        LOG.info("Querying by text: {}", requestModel.getText() );
        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryWebClientResponseModel.class);
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClient
                .method(HttpMethod.valueOf(webClientConfig.getQueryByText().getMethod()))
                .uri(webClientConfig.getQueryByText().getUri())
                .accept(MediaType.valueOf(webClientConfig.getWebClient().getAcceptType()))
                .body(BodyInserters.fromPublisher(Mono.just(requestModel.getText()), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not Authenticated")))
                .onStatus(
                        HttpStatus::is4xxClientError,
                        clientResponse -> Mono.just(new ElasticQueryWebClientException(clientResponse.statusCode().getReasonPhrase())))
                .onStatus(
                        HttpStatus::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(clientResponse.statusCode().getReasonPhrase())));
    }

    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<T>() {
        };
    }
}
