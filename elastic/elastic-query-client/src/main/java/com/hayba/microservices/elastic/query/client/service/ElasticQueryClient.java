package com.hayba.microservices.elastic.query.client.service;


import com.hayba.microservices.elastic.model.index.IndexModel;

import java.util.List;

public interface ElasticQueryClient<T extends IndexModel> {
    T getIndexModelById(String id);
    List<T> getIndexModelByText(String text);
    List<T> getAllIndexModels();
}
