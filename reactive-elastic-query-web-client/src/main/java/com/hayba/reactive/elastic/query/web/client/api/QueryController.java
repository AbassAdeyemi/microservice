package com.hayba.reactive.elastic.query.web.client.api;

import com.hayba.microservices.elastic.query.web.client.common.model.ElasticQueryWebClientRequestModel;
import com.hayba.microservices.elastic.query.web.client.common.model.ElasticQueryWebClientResponseModel;
import com.hayba.reactive.elastic.query.web.client.service.ElasticQueryWebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import javax.validation.Valid;

@Controller
public class QueryController {
    private static final Logger LOG = LoggerFactory.getLogger(QueryController.class);

    private final ElasticQueryWebClient webClient;

    public QueryController(ElasticQueryWebClient webClient) {
        this.webClient = webClient;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/query-by-text")
    public String queryByText(@Valid ElasticQueryWebClientRequestModel requestModel, Model model) {
        Flux<ElasticQueryWebClientResponseModel> responseModel = webClient.getDataByText(requestModel);
        responseModel = responseModel.log();
        IReactiveDataDriverContextVariable reactiveData = new ReactiveDataDriverContextVariable(responseModel, 1);
        model.addAttribute("elasticQueryClientResponseModels", responseModel);
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryClientRequestModel", ElasticQueryWebClientRequestModel.builder().build());
        LOG.info("Returning from reactive client controller for text: {}", requestModel.getText());
        return "home";
    }

}
