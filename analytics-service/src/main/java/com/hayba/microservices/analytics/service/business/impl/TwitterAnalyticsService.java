package com.hayba.microservices.analytics.service.business.impl;

import com.hayba.microservices.analytics.service.business.AnalyticsService;
import com.hayba.microservices.analytics.service.dataaccess.repository.AnalyticsRepository;
import com.hayba.microservices.analytics.service.model.AnalyticsResponseModel;
import com.hayba.microservices.analytics.service.transformer.EntityToResponseModelTransformer;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TwitterAnalyticsService implements AnalyticsService {

    private final AnalyticsRepository analyticsRepository;
    private final EntityToResponseModelTransformer entityToResponseModelTransformer;

    @Override
    public Optional<AnalyticsResponseModel> getAnalytics(String word) {
        return entityToResponseModelTransformer
                .getResponseModel(analyticsRepository.getAnalyticsEntityByWord(word, PageRequest.of(0, 1))
                .stream().findFirst().orElse(null));
    }
}
