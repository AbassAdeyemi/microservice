package com.hayba.microservices.analytics.service.business;

import com.hayba.microservices.analytics.service.model.AnalyticsResponseModel;

import java.util.Optional;

public interface AnalyticsService {

    Optional<AnalyticsResponseModel> getAnalytics(String word);
}
