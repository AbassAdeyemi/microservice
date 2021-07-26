package com.hayba.microservices.query.service.api;

import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceRequestModel;
import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;
import com.hayba.microservices.query.service.business.impl.TwitterElasticQueryService;
import com.hayba.microservices.query.service.model.ElasticQueryServiceAnalyticsResponseModel;
import com.hayba.microservices.query.service.security.TwitterQueryUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/documents")
public class ElasticDocumentController {

    private static final Logger log = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final TwitterElasticQueryService twitterElasticQueryService;

    public ElasticDocumentController(TwitterElasticQueryService twitterElasticQueryService) {
        this.twitterElasticQueryService = twitterElasticQueryService;
    }

    @PostAuthorize("hasPermission('returnObject', 'READ')")
    @Operation(summary = "Get all elastic documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application.json", schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            })
    })
    @GetMapping("")
    public @ResponseBody
    ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocuments() {
        List<ElasticQueryServiceResponseModel> response = twitterElasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} number of document", response.size());
        return ResponseEntity.ok(response);
    }

    @PreAuthorize("hasPermission('#id', 'ElasticQueryServiceResponseModel', 'READ')")
    @PostAuthorize("hasPermission('returnObject', 'READ')")
    @Operation(summary = "Get document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application.json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            })
    })
    @GetMapping("/{id}")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel responseModel = twitterElasticQueryService.getDocumentById(id);
        log.info("Elasticsearch returned document with id: {}", id);
        return ResponseEntity.ok(responseModel);
    }

    @PreAuthorize("hasRole('APP_USER_ROLE') || hasAuthority('SCOPE_APP_USER_ROLE')")
    @Operation(summary = "Get document by text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application.json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            })
    })
    @PostMapping("/get-document-by-text")
    public @ResponseBody
    ResponseEntity<ElasticQueryServiceAnalyticsResponseModel>
    getDocumentsByText(@Valid @RequestBody ElasticQueryServiceRequestModel requestModel,
                       @AuthenticationPrincipal TwitterQueryUser principal,
                       @RegisteredOAuth2AuthorizedClient("keycloak")OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        ElasticQueryServiceAnalyticsResponseModel response =
                twitterElasticQueryService.getDocumentsByText(requestModel.getText(),
                        oAuth2AuthorizedClient.getAccessToken().getTokenValue());
        return ResponseEntity.ok(response);
    }
}
