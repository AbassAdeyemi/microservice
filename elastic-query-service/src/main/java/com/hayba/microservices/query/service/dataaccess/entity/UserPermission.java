package com.hayba.microservices.query.service.dataaccess.entity;

import lombok.Data;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Entity
public class UserPermission {
    @NotNull
    @Id
    private UUID id;
    @NotNull
    private String username;
    @NotNull
    private String documentId;
    @NotNull
    private String permissionType;
}
