package org.apitome.core.service;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;

@ExtendWith(MockitoExtension.class)
public class SimpleServiceTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @RegisterExtension
    private static final WireMockExtension SIMPLE_SERVICE = WireMockExtension.newInstance().options(WireMockConfiguration.wireMockConfig().port(8086)).build();

    private HttpComponentsService simpleService;

    @BeforeEach
    public void setup() {
        ClientConfig testClientConfig = new TestClientConfig();
        this.simpleService = new HttpComponentsService(testClientConfig);
        SIMPLE_SERVICE.stubFor(get("/fernando").withHeader("Content-Type", WireMock.matching("application/json"))).setResponse(aResponse().withBody("{ body: 12 }").build());
    }

    @Test
    public void testWebClient() {
        Mono<String> request = simpleService.get()
                .uri("/fernando")
                .header("Content-Type", "application/json")
                .retrieve()
                .bodyToMono(String.class);

        StepVerifier.create(request)
                .assertNext(res -> log.info("=============================>response: {}", res))
                .verifyComplete();
    }
    
    public static class TestClientConfig implements ClientConfig {

        @Override
        public String getBaseUrl() {
            return "http://localhost:8086";
        }

        @Override
        public int getMaxConnTotal() {
            return 500;
        }

        @Override
        public int getMaxConnPerRoute() {
            return 100;
        }

        @Override
        public long getConnectTimeout() {
            return 1500;
        }

        @Override
        public int getSocketTimeout() {
            return 1500;
        }
    }
}
