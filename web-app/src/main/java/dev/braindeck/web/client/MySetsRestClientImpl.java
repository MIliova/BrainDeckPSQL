package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.NewTermPayload;
import dev.braindeck.web.controller.payload.RestSetPayload;
import dev.braindeck.web.controller.payload.RestUpdateSetPayload;
import dev.braindeck.web.controller.payload.UpdateTermPayload;
import dev.braindeck.web.entity.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MySetsRestClientImpl implements MySetsRestClient {

    private final RestClient restClient;

    @Override
    public Optional<SetEditDto> findById(int id) {
        return Optional.ofNullable(restClient.get()
                .uri("/api/me/set/{id}", id)
                .retrieve()
                .body(SetEditDto.class));
    }

    @Override
    public SetCreatedDto create(String title, String description, Integer termLanguageId, Integer descriptionLanguageId,
                             List<NewTermPayload> terms) {
        return restClient
                .post()
                .uri("/api/me/set")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestSetPayload(title, description, termLanguageId, descriptionLanguageId, terms))
                .retrieve()
                .body(SetCreatedDto.class);
    }

    @Override
    public void update(int id, String title, String description, Integer termLanguageId, Integer descriptionLanguageId,
                       List<UpdateTermPayload> terms) {
        restClient
                .patch()
                .uri("/api/me/set/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestUpdateSetPayload( id, title, description, termLanguageId, descriptionLanguageId, terms))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void delete(int id) {
        restClient
                .delete()
                .uri("/api/me/set/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public List<SetWithCountDto> findAll() {
        return restClient
                .get()
                .uri("/api/me/sets")
                .retrieve()
                .body(new ParameterizedTypeReference<List<SetWithCountDto>>() {});
    }

}
