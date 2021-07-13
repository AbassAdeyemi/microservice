package com.hayba.microservices.query.service.transformer;

import com.hayba.microservices.query.service.dataaccess.entity.UserPermission;
import com.hayba.microservices.query.service.security.PermissionType;
import com.hayba.microservices.query.service.security.TwitterQueryUser;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserPermissionsToUserDetailTransformer {

    public TwitterQueryUser getUserDetails(List<UserPermission> userPermissions) {
        return TwitterQueryUser.builder()
                .username(userPermissions.get(0).getUsername())
                .permissions(userPermissions.stream()
                        .collect(Collectors.toMap(UserPermission::getDocumentId, permission ->
                                PermissionType.valueOf(permission.getPermissionType()))))
                .build();
    }
}
