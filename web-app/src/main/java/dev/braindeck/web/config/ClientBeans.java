package dev.braindeck.web.config;

import dev.braindeck.web.client.LanguagesRestClientImpl;
import dev.braindeck.web.client.MyDraftRestClientImpl;
import dev.braindeck.web.client.SetsRestClientImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class ClientBeans {

    @Bean
    public MyDraftRestClientImpl myDraftRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new MyDraftRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public SetsRestClientImpl setsRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new SetsRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public LanguagesRestClientImpl languagesRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new LanguagesRestClientImpl(RestClient
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
