package com.hayba.microservices.query.service.security;

import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.hayba.microservices.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class QueryServicePermissionEvaluator implements PermissionEvaluator {

    private static final String SUPER_USER_ROLE = "APP_SUPER_USER_ROLE";
    private final HttpServletRequest httpServletRequest;

    @Override
    @SuppressWarnings("unchecked")
    public boolean hasPermission(Authentication authentication, Object targetDomain, Object permission) {
        if(isSuperUser()) {
            return true;
        }
        if(targetDomain instanceof ElasticQueryServiceRequestModel)
            return preAuthorize(authentication, ((ElasticQueryServiceRequestModel) targetDomain).getId(), permission);
        else if (targetDomain instanceof ResponseEntity || targetDomain == null) {
            if(targetDomain == null) {
                return true;
            }
            ElasticQueryServiceAnalyticsResponseModel responseBody =
                    ((ResponseEntity<ElasticQueryServiceAnalyticsResponseModel >)targetDomain).getBody();
            Objects.requireNonNull(responseBody);
            return postAuthorize(authentication, responseBody.getQueryServiceResponseModels(), permission);
        }

        return false;
    }

    private boolean postAuthorize(Authentication authentication, List<ElasticQueryServiceResponseModel> responseBody, Object permission) {
        TwitterQueryUser twitterQueryUser =  (TwitterQueryUser) authentication.getPrincipal();
        for(ElasticQueryServiceResponseModel responseModel : responseBody) {
            PermissionType userPermission = twitterQueryUser.getPermissions().get(responseModel.getId());
            if(!hasPermission((String) permission, userPermission))
                return false;
        }
        return true;
    }

    private boolean preAuthorize(Authentication authentication, String id, Object permission) {
        TwitterQueryUser twitterQueryUser = (TwitterQueryUser) authentication.getPrincipal();
        PermissionType userPermission = twitterQueryUser.getPermissions().get(id);
        return hasPermission((String)permission, userPermission);
    }

    private boolean hasPermission(String requiredPermission, PermissionType userPermission) {
        return userPermission != null && userPermission.getType().equals(requiredPermission);
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return preAuthorize(authentication, (String)targetId, permission);
    }

    private boolean isSuperUser() {
        return httpServletRequest.isUserInRole(SUPER_USER_ROLE);
    }


}
