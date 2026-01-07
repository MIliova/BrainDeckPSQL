package dev.braindeck.web.client;

import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;

import org.springframework.web.client.RestClient;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class SetsRestClientImpl implements SetsRestClient {

    private final RestClient restClient;

    @Override
    public SetShortDto findSetById(int setId) {
        return restClient.get()
                .uri("/api/sets/{setId}", setId)
                .retrieve()
                .body(SetShortDto.class);
    }

    @Override
    public List<SetWithCountDto> findAllSets(int userId) {
        return restClient
                .get()
                .uri("/api/users/{userId}/sets", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SetWithCountDto>>() {});
    }

}
