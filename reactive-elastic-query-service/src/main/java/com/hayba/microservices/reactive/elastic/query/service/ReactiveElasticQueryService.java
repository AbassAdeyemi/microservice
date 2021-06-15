package com.hayba.microservices.reactive.elastic.query.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.hayba.microservices")
public class ReactiveElasticQueryService {
    public static void main(String[] args) {
        SpringApplication.run(ReactiveElasticQueryService.class, args);
    }
}
