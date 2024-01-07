package org.apitome.core.service;

public interface ClientConfig {

    String getBaseUrl();

    int getMaxConnTotal();

    int getMaxConnPerRoute();

    long getConnectTimeout();

    int getSocketTimeout();
}
