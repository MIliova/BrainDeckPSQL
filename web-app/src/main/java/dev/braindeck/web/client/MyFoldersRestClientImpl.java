package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.*;
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
public class MyFoldersRestClientImpl implements MyFoldersRestClient {

    private final RestClient restClient;

    @Override
    public Optional<FolderEditDto> findById(int id) {
        return Optional.ofNullable(restClient.get()
                .uri("/api/users/me/folder/{id}", id)
                .retrieve()
                .body(FolderEditDto.class));
    }



    @Override
    public FolderCreatedDto create(String title) {
        return restClient
                .post()
                .uri("/api/users/me/folder")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestFolderPayload(title))
                .retrieve()
                .body(FolderCreatedDto.class);
    }

    @Override
    public void update(int id, String title) {
        restClient
                .patch()
                .uri("/api/users/me/folder/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestUpdateFolderPayload( id, title))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void delete(int id) {
        restClient
                .delete()
                .uri("/api/users/me/folder/{id}", id)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public List<FolderWithCountDto> findAll() {
        return restClient
                .get()
                .uri("/api/users/me/folders")
                .retrieve()
                .body(new ParameterizedTypeReference<List<FolderWithCountDto>>() {});
    }


}
