package com.hayba.microservices.query.service.security;

import com.hayba.microservices.config.ElasticQueryServiceConfigData;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Qualifier("elastic-query-service-audience-validator")
public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
    private final ElasticQueryServiceConfigData configData;
    @Override
    public OAuth2TokenValidatorResult validate(Jwt jwt) {
        if(jwt.getAudience().equals(configData.getCustomAudience()))
        return OAuth2TokenValidatorResult.success();
        else{
            OAuth2Error oAuth2Error = new OAuth2Error("invalid-token",
                    "The required audience "+ configData.getCustomAudience()+" is missing!", null);

            return OAuth2TokenValidatorResult.failure(oAuth2Error);
        }
    }
}
