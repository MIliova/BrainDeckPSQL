package dev.braindeck.web.config;

import dev.braindeck.web.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@Configuration
public class ClientBeans {

    @Bean
    public LanguagesRestClientImpl languagesRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new LanguagesRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public MyDraftRestClientImpl myDraftRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new MyDraftRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

    @Bean
    public MySetsRestClientImpl mySetsRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new MySetsRestClientImpl(RestClient
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
    public UserRestClientImpl userRestClient(@Value("${braindeck.api.uri}") String apiBaseUrl) {
        return new UserRestClientImpl(RestClient
                .builder()
                .baseUrl(apiBaseUrl)
                .build());
    }

//    @Bean
//    public MyTermsRestClientImpl myTermsRestClientImpl(@Value("${braindeck.api.uri}") String apiBaseUrl) {
//        return new MyTermsRestClientImpl(RestClient
//                .builder()
//                .baseUrl(apiBaseUrl)
//                .build());
//    }

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
