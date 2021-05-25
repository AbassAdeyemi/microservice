package com.hayba.microservices.query.service.api;

import com.hayba.microservices.query.service.business.impl.TwitterElasticQueryService;
import com.hayba.microservices.query.service.model.ElasticQueryServiceRequestModel;
import com.hayba.microservices.query.service.model.ElasticQueryServiceResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

//@RequestMapping("/documents")
@RestController
public class ElasticDocumentController {

    private static final Logger log = LoggerFactory.getLogger(ElasticDocumentController.class);

    private final TwitterElasticQueryService twitterElasticQueryService;

    public ElasticDocumentController(TwitterElasticQueryService twitterElasticQueryService) {
        this.twitterElasticQueryService = twitterElasticQueryService;
    }

    @Operation(summary = "Get all elastic documents")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application.json", schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            })
    })
    @GetMapping("/documents")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocuments() {
        List<ElasticQueryServiceResponseModel> response = twitterElasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} number of document", response.size());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get document by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application.json",
                    schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            })
    })
    @GetMapping("/documents/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel responseModel = twitterElasticQueryService.getDocumentById(id);
        log.info("Elasticsearch returned document with id: {}", id);
        return ResponseEntity.ok(responseModel);
    }

    @Operation(summary = "Get document by text")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(mediaType = "application.json",
                            schema = @Schema(implementation = ElasticQueryServiceResponseModel.class))
            })
    })
    @PostMapping("/documents/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentsByText(@Valid @RequestBody ElasticQueryServiceRequestModel requestModel) {
        List<ElasticQueryServiceResponseModel> response =
                twitterElasticQueryService.getDocumentsByText(requestModel.getText());
        return ResponseEntity.ok(response);
    }
}
