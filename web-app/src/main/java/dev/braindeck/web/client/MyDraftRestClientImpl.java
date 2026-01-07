package dev.braindeck.web.client;

import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.DraftDto;
import dev.braindeck.web.entity.SetCreatedDto;
import dev.braindeck.web.entity.SetDto;
import dev.braindeck.web.entity.SetFullDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MyDraftRestClientImpl implements MyDraftRestClient {

    private final RestClient restClient;

    @Override
    public Optional<DraftDto> getOrCreate() {
        return Optional.ofNullable(restClient.get()
                .uri("/api/me/draft")
                .retrieve()
                .body(DraftDto.class));
    }

    @Override
    public SetCreatedDto createFromDraft(
            int draftId,
            String title,
            String description,
            Integer termLanguageId,
            Integer descriptionLanguageId,
            List<NewTermPayload> terms) {
        return restClient
                .post()
                .uri("/api/me/draft/{draftId}/convert", draftId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new RestSetPayload(title,  description,  termLanguageId,  descriptionLanguageId, terms))
                .retrieve()
                .body(SetCreatedDto.class);
    }

}
