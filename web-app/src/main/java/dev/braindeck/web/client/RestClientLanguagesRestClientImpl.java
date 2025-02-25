package dev.braindeck.web.client;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.*;

@RequiredArgsConstructor
public class RestClientLanguagesRestClientImpl implements LanguagesRestClient {

    private final RestClient restClient;

    private static final ParameterizedTypeReference<Map<String, Map<Integer, String>>> LANGUAGES_TYPE_REFERENCE =
            new ParameterizedTypeReference<>() {
            };

    @Override
    public  Map<String, Map<Integer, String>>  findAllByTypes(){
        return this.restClient
                .get()
                .uri("/api/languages")
                .retrieve()
                .body(LANGUAGES_TYPE_REFERENCE);
    }
}
