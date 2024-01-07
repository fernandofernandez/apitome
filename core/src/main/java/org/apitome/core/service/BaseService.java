package org.apitome.core.service;

import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

public class BaseService {

    protected final WebClient webClient;

    public BaseService(String baseUrl, ClientHttpConnector clientConnector) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(clientConnector)
                .build();
    }

    protected  WebClient getClient() {
        return webClient;
    }

    public WebClient.RequestHeadersUriSpec<?> get() {
        return webClient.get();
    }

    public WebClient.RequestBodyUriSpec post() {
        return webClient.post();
    }

    public WebClient.RequestBodyUriSpec put() {
        return webClient.put();
    }

    public WebClient.RequestBodyUriSpec patch() {
        return webClient.patch();
    }

    public WebClient.RequestHeadersUriSpec<?> delete() {
        return webClient.delete();
    }

    public WebClient.RequestHeadersUriSpec<?> head() {
        return webClient.head();
    }

    public WebClient.RequestHeadersUriSpec<?> options() {
        return webClient.options();
    }
}
