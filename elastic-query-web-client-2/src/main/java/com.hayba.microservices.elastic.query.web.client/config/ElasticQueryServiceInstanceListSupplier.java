package com.hayba.microservices.elastic.query.web.client.config;

import com.hayba.microservices.config.ElasticQueryWebClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Configuration
@Primary
public class ElasticQueryServiceInstanceListSupplier implements ServiceInstanceListSupplier {

    private final ElasticQueryWebClientConfig.WebClient webClientConfigData;

    public ElasticQueryServiceInstanceListSupplier(ElasticQueryWebClientConfig webClientConfigData) {
        this.webClientConfigData = webClientConfigData.getWebClient();
    }

    @Override
    public String getServiceId() {
        return webClientConfigData.getServiceId();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        log.info("Instances: {}", webClientConfigData.getInstances());
        return Flux.just(webClientConfigData.getInstances()
                .stream().map(instance -> new DefaultServiceInstance(
                        instance.getId(), getServiceId(), instance.getHost(), instance.getPort(), false))
                .collect(Collectors.toList()));
    }
}
