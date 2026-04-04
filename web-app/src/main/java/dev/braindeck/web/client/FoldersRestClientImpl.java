package dev.braindeck.web.client;

import dev.braindeck.web.entity.SetShortDto;
import dev.braindeck.web.entity.SetWithCountDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class FoldersRestClientImpl implements SetsRestClient {

    private final RestClient restClient;

    @Override
    public SetShortDto findById(int id) {
        return restClient.get()
                .uri("/api/folder/{id}", id)
                .retrieve()
                .body(SetShortDto.class);
    }

    @Override
    public List<SetWithCountDto> findAll(int userId) {
        return restClient
                .get()
                .uri("/api/user/{userId}/folders", userId)
                .retrieve()
                .body(new ParameterizedTypeReference<List<SetWithCountDto>>() {});
    }



}
