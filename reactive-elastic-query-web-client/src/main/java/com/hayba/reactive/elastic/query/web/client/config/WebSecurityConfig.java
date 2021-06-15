package com.hayba.reactive.elastic.query.web.client.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class WebSecurityConfig {

    public SecurityWebFilterChain webFluxSecurityConfig(ServerHttpSecurity httpSecurity) {
        httpSecurity
                .authorizeExchange()
                .anyExchange()
                .permitAll();
        httpSecurity.csrf().disable();
        return httpSecurity.build();
    }
}
