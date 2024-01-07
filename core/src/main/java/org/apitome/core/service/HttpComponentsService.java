package org.apitome.core.service;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.HttpComponentsClientHttpConnector;

import java.util.concurrent.TimeUnit;

public class HttpComponentsService extends BaseService implements Service {

    public HttpComponentsService(ClientConfig clientConfig) {
        super(clientConfig.getBaseUrl(), createClientConnector(clientConfig));
    }

    private static ClientHttpConnector createClientConnector(ClientConfig clientConfig) {
        ConnectionConfig connConfig = ConnectionConfig.custom()
                .setConnectTimeout(clientConfig.getConnectTimeout(), TimeUnit.MILLISECONDS)
                .setSocketTimeout(clientConfig.getSocketTimeout(), TimeUnit.MILLISECONDS)
                .build();
        AsyncClientConnectionManager connManager = PoolingAsyncClientConnectionManagerBuilder.create()
                .setMaxConnTotal(clientConfig.getMaxConnTotal())
                .setMaxConnPerRoute(clientConfig.getMaxConnPerRoute())
                .setDefaultConnectionConfig(connConfig)
                .build();
        CloseableHttpAsyncClient httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionManager(connManager)
                .build();
        return new HttpComponentsClientHttpConnector(httpAsyncClient);
    }
}
