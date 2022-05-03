package com.bootcamp.personal.passiveaccounts.service;

import com.bootcamp.personal.passiveaccounts.MsPersonalPassiveAccountsApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebClientService {

    private final WebClient webClient;

    public WebClientService(WebClient.Builder webBuilder) {
        this.webClient = webBuilder
                .baseUrl(MsPersonalPassiveAccountsApplication.getApiGateway())
                .build();
    }

    public WebClient getWebClient() {
        return webClient;
    }

}
