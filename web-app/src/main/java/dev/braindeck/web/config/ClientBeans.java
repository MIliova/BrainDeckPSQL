package dev.braindeck.web.config;

import dev.braindeck.web.client.RestClientLanguagesRestClientImpl;
import dev.braindeck.web.client.RestClientSetsRestClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientSetsRestClientImpl SetsRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new RestClientSetsRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public RestClientLanguagesRestClientImpl LanguagesRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new RestClientLanguagesRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }
}
