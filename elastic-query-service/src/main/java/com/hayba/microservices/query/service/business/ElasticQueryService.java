package com.hayba.microservices.query.service.business;


import com.hayba.microservices.elastic.query.service.common.model.ElasticQueryServiceResponseModel;

import java.util.List;

public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);

    List<ElasticQueryServiceResponseModel> getDocumentsByText(String text);

    List<ElasticQueryServiceResponseModel> getAllDocuments();
}
