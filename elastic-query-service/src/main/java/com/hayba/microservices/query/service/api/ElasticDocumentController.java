package com.hayba.microservices.query.service.api;

import com.hayba.microservices.query.service.business.impl.TwitterElasticQueryService;
import com.hayba.microservices.query.service.model.ElasticQueryServiceRequestModel;
import com.hayba.microservices.query.service.model.ElasticQueryServiceResponseModel;
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

    @GetMapping("/documents")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocuments() {
        List<ElasticQueryServiceResponseModel> response = twitterElasticQueryService.getAllDocuments();
        log.info("Elasticsearch returned {} number of document", response.size());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents/{id}")
    public ResponseEntity<ElasticQueryServiceResponseModel> getDocumentById(@PathVariable @NotEmpty String id) {
        ElasticQueryServiceResponseModel responseModel = twitterElasticQueryService.getDocumentById(id);
        log.info("Elasticsearch returned document with id: {}", id);
        return ResponseEntity.ok(responseModel);
    }

    @PostMapping("/documents/get-document-by-text")
    public ResponseEntity<List<ElasticQueryServiceResponseModel>> getDocumentsByText(@Valid @RequestBody ElasticQueryServiceRequestModel requestModel) {
        List<ElasticQueryServiceResponseModel> response =
                twitterElasticQueryService.getDocumentsByText(requestModel.getText());
        return ResponseEntity.ok(response);
    }
}
