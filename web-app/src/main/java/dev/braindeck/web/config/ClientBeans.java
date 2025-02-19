package dev.braindeck.web.config;

import dev.braindeck.web.client.RestClientLanguagesRestClientImpl;
import dev.braindeck.web.client.RestClientSetsRestClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class ClientBeans {

    @Bean
    public RestClientSetsRestClientImpl setsRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new RestClientSetsRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public RestClientLanguagesRestClientImpl languagesRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new RestClientLanguagesRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA: ");

        return filter;
    }


}
