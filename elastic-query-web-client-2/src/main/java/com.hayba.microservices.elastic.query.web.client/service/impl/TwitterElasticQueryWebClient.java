package com.hayba.microservices.elastic.query.web.client.service.impl;

import com.hayba.microservices.config.ElasticQueryWebClientConfig;
import com.hayba.microservices.elastic.query.web.client.common.exception.ElasticQueryWebClientException;
import com.hayba.microservices.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.hayba.microservices.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.hayba.microservices.elastic.query.web.client.service.ElasticQueryWebClient;
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
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

    private static final Logger LOG = LoggerFactory.getLogger(TwitterElasticQueryWebClient.class);
    private WebClient.Builder webClientBuilder;
    private ElasticQueryWebClientConfig webClientConfig;

    public TwitterElasticQueryWebClient(@Qualifier("webClientBuilder") WebClient.Builder webClientBuilder, ElasticQueryWebClientConfig webClientConfig) {
        this.webClientBuilder = webClientBuilder;
        this.webClientConfig = webClientConfig;
    }

    @Override
    public List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
       LOG.info("Querying by text: {}", requestModel.getText());
       return getWebClient(requestModel)
               .bodyToFlux(ElasticQueryWebClientResponseModel.class)
               .collectList()
               .block();
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClientBuilder
                .build()
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
