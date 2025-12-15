package dev.braindeck.web.client;

import dev.braindeck.web.entity.LanguagesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class LanguagesRestClientImpl implements LanguagesRestClient {

    private final RestClient restClient;

    @Override
    public LanguagesDto findAllByTypes(){
        return restClient
                .get()
                .uri("/api/languages")
                .retrieve()
                .body(LanguagesDto.class);
    }
}
