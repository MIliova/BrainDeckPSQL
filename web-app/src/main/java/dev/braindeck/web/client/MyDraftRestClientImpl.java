package dev.braindeck.web.client;

import dev.braindeck.web.controller.exception.BadRequestException;
import dev.braindeck.web.controller.exception.ProblemDetailException;
import dev.braindeck.web.controller.payload.*;
import dev.braindeck.web.entity.DraftDto;
import dev.braindeck.web.entity.SetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;

import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class MyDraftRestClientImpl implements MyDraftRestClient {

    private final RestClient restClient;

    @Override
    public Optional<DraftDto> get() {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri("/api/me/draft")
                    .retrieve()
                    .body(DraftDto.class));
        } catch (RestClientResponseException e) {
            ProblemDetail problemDetail = e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new NoSuchElementException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

    @Override
    public SetDto createFromDraft(
            int draftId,
            String title,
            String description,
            Integer termLanguageId,
            Integer descriptionLanguageId,
            List<NewTermPayload> terms) {
        try {
            return restClient
                    .post()
                    .uri("/api/me/draft/{draftId}/convert", draftId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new RestSetPayload(title,  description,  termLanguageId,  descriptionLanguageId, terms))
                    .retrieve()
                    .body(SetDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            ProblemDetail problemDetail =  e.getResponseBodyAs(ProblemDetail.class);
            if(problemDetail != null) {
                throw new BadRequestException(String.valueOf(Objects.requireNonNull(problemDetail.getProperties()).get("errors")));
            }
            throw new ProblemDetailException("Problem detail is null");
        }
    }

}
