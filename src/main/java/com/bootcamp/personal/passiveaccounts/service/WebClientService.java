package com.bootcamp.personal.passiveaccounts.service;

import com.bootcamp.personal.passiveaccounts.MsPersonalPassiveAccountsApplication;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WebClientService {

    private WebClient webClient;
    private final WebClient.Builder webBuilder;

    public WebClientService(WebClient.Builder webBuilder) {
        this.webBuilder = webBuilder;
        this.webClient = null;
    }

    public WebClient getWebClient() {
        if(webClient == null) {
            this.webClient = webBuilder
                    .baseUrl(MsPersonalPassiveAccountsApplication.getApiGateway())
                    .build();
        }
        return webClient;
    }

}
